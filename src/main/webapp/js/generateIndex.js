function configureEditors(error) {
    $(document).ready(function(){
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

            $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
                var target = $(e.target).attr("href").substring(1); // activated tab
                editors[target].refresh();
            });

        } else {
            CodeMirror.fromTextArea(document.getElementById("output-error"), {
                lineNumbers: true, mode: "text/plain"
            });
        }

        CodeMirror.fromTextArea(document.getElementById("queries"), {
            lineNumbers: true, mode: "text/x-sql"
        });
    });
}

