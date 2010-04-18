package com.xmlcalabash.util;

import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcMessageListener;
import com.xmlcalabash.core.XProcRunnable;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.trans.XPathException;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import java.net.URI;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: Dec 18, 2009
 * Time: 8:18:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultXProcMessageListener implements XProcMessageListener {
    private static Logger defaultLogger = Logger.getLogger("com.xmlcalabash");
    private Logger log = defaultLogger;

    public void error(XProcRunnable step, XdmNode node, String message, QName code) {
        if (step != null) {
            log = Logger.getLogger(step.getClass().getName());
        } else {
            log = defaultLogger;
        }

        log.severe(message(step, node, message, code));
    }

    public void error(Throwable exception) {
        StructuredQName qCode = null;
        SourceLocator loc = null;
        String message = "";

        if (exception instanceof XPathException) {
            qCode = ((XPathException) exception).getErrorCodeQName();
        }

        if (exception instanceof TransformerException) {
            TransformerException tx = (TransformerException) exception;
            if (qCode == null && tx.getException() instanceof XPathException) {
                qCode = ((XPathException) tx.getException()).getErrorCodeQName();
            }

            if (tx.getLocator() != null) {
                loc = tx.getLocator();
                boolean done = false;
                while (!done && loc == null) {
                    if (tx.getException() instanceof TransformerException) {
                        tx = (TransformerException) tx.getException();
                        loc = tx.getLocator();
                    } else if (exception.getCause() instanceof TransformerException) {
                        tx = (TransformerException) exception.getCause();
                        loc = tx.getLocator();
                    } else {
                        done = true;
                    }
                }
            }
        }

        if (exception instanceof XProcException) {
            XProcException err = (XProcException) exception;
            loc = err.getLocator();
            if (err.getErrorCode() != null) {
                QName n = err.getErrorCode();
                qCode = new StructuredQName(n.getPrefix(),n.getNamespaceURI(),n.getLocalName());
            }
            if (err.getStep() != null) {
                message = message + err.getStep() + ":";
            }
        }

        if (loc != null) {
            if (loc.getSystemId() != null && !"".equals(loc.getSystemId())) {
                message = message + loc.getSystemId() + ":";
            }
            if (loc.getLineNumber() != -1) {
                message = message + loc.getLineNumber() + ":";
            }
            if (loc.getColumnNumber() != -1) {
                message = message + loc.getColumnNumber() + ":";
            }
        }

        if (qCode != null) {
            message = message + qCode.getDisplayName() + ":";
        }

        log.severe(message + exception.getMessage());
    }

    public void warning(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = Logger.getLogger(step.getClass().getName());
        } else {
            log = defaultLogger;
        }
        log.warning(message(step, node, message));
    }

    public void info(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = Logger.getLogger(step.getClass().getName());
        } else {
            log = defaultLogger;
        }
        log.info(message(step, node, message));
    }

    public void fine(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = Logger.getLogger(step.getClass().getName());
        } else {
            log = defaultLogger;
        }
        log.fine(message(step, node, message));
    }

    public void finer(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = Logger.getLogger(step.getClass().getName());
        } else {
            log = defaultLogger;
        }
        log.finer(message(step, node, message));
    }

    public void finest(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = Logger.getLogger(step.getClass().getName());
        } else {
            log = defaultLogger;
        }
        log.finest(message(step, node, message));
    }

    private String message(XProcRunnable step, XdmNode node, String message) {
        return message(step, node, message, null);
    }

    private String message(XProcRunnable step, XdmNode node, String message, QName code) {
        String prefix = "";
        if (node != null) {
            URI cwd = URIUtils.cwdAsURI();
            String systemId = cwd.relativize(node.getBaseURI()).toASCIIString();
            int line = node.getLineNumber();
            int col = node.getColumnNumber();

            if (systemId != null && !"".equals(systemId)) {
                prefix = prefix + systemId + ":";
            }
            if (line != -1) {
                prefix = prefix + line + ":";
            }
            if (col != -1) {
                prefix = prefix + col + ":";
            }
        }

        return prefix + message;
    }
}
