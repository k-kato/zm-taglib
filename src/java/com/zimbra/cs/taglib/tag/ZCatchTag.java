package com.zimbra.cs.taglib.tag;

import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.tag.common.core.CatchTag;

/**
 * Subclass of JSTL CatchTag to temporarily workaround Apache bug https://bz.apache.org/bugzilla/show_bug.cgi?id=58178.
 * Sets var in session scope so it can be accessed later in the page
 */
public class ZCatchTag extends CatchTag {

    public ZCatchTag() {
        super();
        init();
    }

    private void init() {
        var = null;
    }

    // *********************************************************************
    // Private state

    private String var; // tag attribute
    private boolean caught; // internal status

    // *********************************************************************
    // Tag logic

    @Override
    public int doStartTag() {
        caught = false;
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public void doCatch(Throwable t) {
        if (var != null) {
            pageContext.setAttribute(var, t, PageContext.PAGE_SCOPE);
            pageContext.setAttribute(var, t, PageContext.SESSION_SCOPE);
        }
        caught = true;
    }

    @Override
    public void doFinally() {
        if (var != null && !caught) {
            pageContext.removeAttribute(var, PageContext.PAGE_SCOPE);
            pageContext.removeAttribute(var, PageContext.SESSION_SCOPE);
        }
    }

    // *********************************************************************
    // Attribute accessors

    @Override
    public void setVar(String var) {
        this.var = var;
    }

}
