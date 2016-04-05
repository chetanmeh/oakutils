function configureCNDMode(ruleNames) {
    CodeMirror.defineSimpleMode("cnd", {
        // The start state contains the rules that are intially used
        start: [
            // The regex matches the token, the token property contains the type
            {regex: /"(?:[^\\]|\\.)*?"/, token: "string"},
            {regex: /(?:type|compatVersion|async|evaluatePathRestrictions|codec|includedPaths|excludedPaths|name|analyzed|ordered|propertyIndex|nullCheckEnabled|type|path|relativeNode|jcr:primaryType)\b/, token: "keyword"},
            {regex: /(?:indexRules|properties|aggregates)\b/, token: "variable-3"},
            {regex: new RegExp("(?:oak:QueryIndexDefinition|"+ruleNames+")\\b"), token: "variable-2"},
            {regex: /true|false/, token: "atom"},
            {regex: /0x[a-f\d]+|[-+]?(?:\.\d+|\d+\.?\d*)(?:e[-+]?\d+)?/i, token: "number"},
            {regex: /\/(?:[^\\]|\\.)*?\//, token: "variable-3"},
            {regex: /[-+\/*=<>!]+/, token: "operator"},
            {regex: /[a-z$][\w$]*/, token: "variable"}

        ]
    });

}

function configureEditors(error,ruleNames) {
    $(document).ready(function(){
        if(!error) {
            configureCNDMode(ruleNames);
            var editors = {
                'output-text': CodeMirror.fromTextArea(document.getElementById("output-display-text"), {
                    lineNumbers: true, mode: "cnd"
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

