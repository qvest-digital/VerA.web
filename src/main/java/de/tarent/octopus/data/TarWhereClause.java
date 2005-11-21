/* $Id: TarWhereClause.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Stellt eine SQL WHERE Bedingung dar.
 * Die Klasse wird über einen Postfix Ausdruck Initialisiert. Und kann einen String mit einem SQL Ausdruck liefern.
 *
 * <b>Postfix:</b>
 * Postfix ist eine Möglichkeit einen Ausdrucksbaum, wie in einer flachen Liste eindeutig zu speichern, ohne Klammerung zu benötigen.
 * <br>Der Ausdruck ((name = 'Sebastian') and (id = '2'))  sieht in Postfix z.B. so aus: name 'Sebastian' = id '2' "=" "AND"
 * Dieses Modell bietet die Möglichkeit Suchausdrücke in einer flachen Struktur zu speichern und ihre logische Struktur dabei zu erhalten. 
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
            if (stack.size() > 1)
                throw new TarMalformedWhereClauseException("Der Ausdruck ist keine gültige Postfix Notation. Es sind zu wehnig Operatoren für die Operanden vorhanden.");

            return (TarWhereNode) stack.remove(0);
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            throw new TarMalformedWhereClauseException("Der Ausdruck ist keine gültige Postfix Notation. Es sind nicht genügend Operanden für die Operatoren vorhanden.");
        }
    }

    private StringBuffer inOrderTraverse(TarWhereNode currRootNode) {
        StringBuffer out = new StringBuffer();
        if (currRootNode != null) {

            if (currRootNode.isDoubleOperator())
                out.append("(");

            StringBuffer left = inOrderTraverse(currRootNode.getFistChild());
            if (left.length() > 0)
                out.append(left).append(" ");

            out.append(currRootNode.getValue());

            StringBuffer right = inOrderTraverse(currRootNode.getSecondChild());
            if (right.length() > 0)
                out.append(" ").append(right);

            if (currRootNode.isDoubleOperator())
                out.append(")");
        }
        return out;
    }
}
