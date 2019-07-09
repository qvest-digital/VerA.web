package de.tarent.octopus.request.servlet;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Attaches a default charset of UTF-8 to all requests that do not yet
 * have one. Inspired by CharacterEncodingFilter by Craig McClanahan.
 *
 * @author mirabilos (t.glaser@tarent.de)
 */
public class DefaultUTF8Filter implements Filter {
    /**
     * Sets the encoding if none is set.
     *
     * @param request  ServletRequest to act upon
     * @param response ServletResponse to be created
     * @param chain    current FilterChain
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet error occurs
     */
    //@Override
    public void doFilter(final ServletRequest request,
      final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {
        if (request.getCharacterEncoding() == null ||
          "".equals(request.getCharacterEncoding())) {
            request.setCharacterEncoding("UTF-8");
        }

        /* pass control on to the next filter */
        chain.doFilter(request, response);
    }

    //@Override
    public void init(FilterConfig config) {
        /* nothing to do here */
    }

    //@Override
    public void destroy() {
        /* nothing to do here */
    }
}
