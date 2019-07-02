package tags;


import util.Json;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.*;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

public class JsonTag extends SimpleTagSupport {
    private String vars;

    public void setVars(String vars) {
        this.vars = vars;
    }

    public String getVars() {
        return vars;
    }

    @Override
    public void doTag() throws JspException, IOException {
        StringWriter writer = new StringWriter();
        JspFragment jspBody = getJspBody();
        jspBody.invoke(writer);
        String content = writer.toString();
        Json json;
        if (this.vars != null)
            json = new Json(getJspContext().getAttribute(vars), content);
        else json = new Json(getJspContext(), content);
        try {
            content = json.fromContext();
            getJspContext().getOut().print(content);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

