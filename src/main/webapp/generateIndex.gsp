<%
    def QUERY_DEFAULT = '''
#Paste your queries here

select * from [nt:base] where foo = 'bar'
    '''

    String queryText = request.getParameter("queries") ?: QUERY_DEFAULT;
    def error
    def indexNodeState = org.apache.jackrabbit.oak.plugins.memory.EmptyNodeState.EMPTY_NODE
    try {
        indexNodeState = com.chetanmeh.oak.index.config.generator.IndexConfigGeneratorHelper.getIndexConfig(queryText)
    }catch (Throwable t){
        error = com.google.common.base.Throwables.getStackTraceAsString(t)
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Oak Utilities : Index Definition Generator</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <link rel="stylesheet" href="/codemirror/lib/codemirror.css">
</head>
<body>
<div class="container">
    <div class="row">
        <h1 class="display-1 text-center">Index Definition Generator</h1>
        <p class="lead">Generates an index definition for a given set of queries</p>
        <form method="POST">
            <div class="form-group">
                <label for="queries">Queries</label>
                <textarea class="form-control" rows="3" name="queries" id="queries">${queryText}</textarea>
            </div>
            <input type="submit" value="Generate">
        </form>
        <a href="/">Back</a>
    </div>

    <% if (error == null){%>
    <div class="row">
        <ul class="nav nav-tabs">
            <li class="active"><a data-toggle="tab" href="#output-text">Text</a></li>
            <li><a data-toggle="tab" href="#output-json">JSON</a></li>
            <li><a data-toggle="tab" href="#output-xml">XML</a></li>
        </ul>
        <div class="tab-content">
            <div id="output-text" class="tab-pane fade in active">
                <textarea id="output-display-text">${com.chetanmeh.oak.state.export.NodeStateExporter.toCND(indexNodeState)}</textarea>
            </div>
            <div id="output-json" class="tab-pane fade">
                <textarea id="output-display-json">${com.chetanmeh.oak.state.export.NodeStateExporter.toJson(indexNodeState)}</textarea>
            </div>
            <div id="output-xml" class="tab-pane fade">
                <textarea id="output-display-xml">${com.chetanmeh.oak.state.export.NodeStateExporter.toXml(indexNodeState)}</textarea>
            </div>
        </div>
    <% } else { %>
        <div class="row">
            <textarea id="output-error">${error}</textarea>
        </div>
    <% } %>
    </div>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
        integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous">
</script>
<script src="/codemirror/lib/codemirror.js"></script>
<script src="/codemirror/mode/xml.js"></script>
<script src="/codemirror/mode/javascript.js"></script>
<script>
    jQuery(document).ready(function(){
        var error = ${error != null}
        if(!error) {
            var editors = {
                'output-text': CodeMirror.fromTextArea(document.getElementById("output-display-text"), {
                    lineNumbers: true
                }),
                'output-json': CodeMirror.fromTextArea(document.getElementById("output-display-json"), {
                    lineNumbers: true, mode: "application/json"
                }),
                'output-xml': CodeMirror.fromTextArea(document.getElementById("output-display-xml"), {
                    lineNumbers: true, mode: "application/xml"
                })
            };

            jQuery('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
                var target = jQuery(e.target).attr("href").substring(1); // activated tab
                editors[target].refresh();
            });

        } else {
            CodeMirror.fromTextArea(document.getElementById("output-error"), {
                lineNumbers: true, mode: "text/plain"
            });
        }
    })
</script>
</body>
</html>