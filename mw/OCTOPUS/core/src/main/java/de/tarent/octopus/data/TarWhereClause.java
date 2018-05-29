package de.tarent.octopus.data;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Stellt eine SQL WHERE Bedingung dar.
 * Die Klasse wird über einen Postfix Ausdruck Initialisiert. Und kann einen String mit einem SQL Ausdruck liefern.
 *
 * <b>Postfix:</b>
 * Postfix ist eine Möglichkeit einen Ausdrucksbaum, wie in einer flachen Liste eindeutig zu speichern, ohne Klammerung zu
 * benötigen.
 * <br>Der Ausdruck ((name = 'Sebastian') and (id = '2'))  sieht in Postfix z.B. so aus: name 'Sebastian' = id '2' "=" "AND"
 * Dieses Modell bietet die Möglichkeit Suchausdrücke in einer flachen Struktur zu speichern und ihre logische Struktur dabei
 * zu erhalten.
 * Letzteres wäre bei der Repräsentation in einem String nicht der Fall.
 *
 * <br><br>
 * Die Klassen erkennen SQL-Schlüsselwörter in der Token Liste:
 * <br>Zwei Operanden: "=", "and", "in", "like", "or"
 * <br>Ein Operand: "not"
 *
 * @see TarWhereNode
 */
public class TarWhereClause {
    TarWhereNode rootNode;

    /**
     * Initialisaierung mit einer Liste von Tokens in Postfix Reihenfolge.
     *
     * Wenn sich aus der Liste kein gültiger Baum aufbauen lässt,
     * wirde eine Exception geworfen.
     */
    public TarWhereClause(String[] postfixTokenList) throws TarMalformedWhereClauseException {

        rootNode = getTree(postfixTokenList);
    }

    /**
     * Initialisaierung mit einer Liste von Knotenobjekten in Postfix Reihenfolge.
     *
     * Wenn sich aus der Liste kein gültiger Baum aufbauen lässt,
     * wirde eine Exception geworfen.
     */
    public TarWhereClause(TarWhereNode[] postfixTokenList) throws TarMalformedWhereClauseException {
        rootNode = getTree(postfixTokenList);
    }

    /**
     * Liefert einen String mit einem SQL Ausdruck zurück.
     */
    public String toString() {
        return inOrderTraverse(rootNode).toString();
    }

    private TarWhereNode getTree(String[] postfixTokenList) throws TarMalformedWhereClauseException {
        TarWhereNode[] whereNodeList = new TarWhereNode[postfixTokenList.length];

        for (int i = 0; i < postfixTokenList.length; i++) {
            whereNodeList[i] = new TarWhereNode(postfixTokenList[i]);
        }

        return getTree(whereNodeList);
    }

    private TarWhereNode getTree(TarWhereNode[] postfixTokenList) throws TarMalformedWhereClauseException {
        try {
            List stack = new ArrayList();
            for (int i = 0; i < postfixTokenList.length; i++) {
                TarWhereNode newNode = postfixTokenList[i];
                if (newNode.isOperator()) {
                    newNode.setSecondChild((TarWhereNode) stack.remove(stack.size() - 1));
                    if (newNode.isDoubleOperator()) {
                        newNode.setFirstChild((TarWhereNode) stack.remove(stack.size() - 1));
                    }
                }
                stack.add(newNode);
            }
            if (stack.size() > 1) {
                throw new TarMalformedWhereClauseException(
                  "Der Ausdruck ist keine gültige Postfix Notation. Es sind zu wehnig Operatoren für die Operanden " +
                    "vorhanden.");
            }

            return (TarWhereNode) stack.remove(0);
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            throw new TarMalformedWhereClauseException(
              "Der Ausdruck ist keine gültige Postfix Notation. Es sind nicht genügend Operanden für die Operatoren " +
                "vorhanden.");
        }
    }

    private StringBuffer inOrderTraverse(TarWhereNode currRootNode) {
        StringBuffer out = new StringBuffer();
        if (currRootNode != null) {

            if (currRootNode.isDoubleOperator()) {
                out.append("(");
            }

            StringBuffer left = inOrderTraverse(currRootNode.getFistChild());
            if (left.length() > 0) {
                out.append(left).append(" ");
            }

            out.append(currRootNode.getValue());

            StringBuffer right = inOrderTraverse(currRootNode.getSecondChild());
            if (right.length() > 0) {
                out.append(" ").append(right);
            }

            if (currRootNode.isDoubleOperator()) {
                out.append(")");
            }
        }
        return out;
    }
}
