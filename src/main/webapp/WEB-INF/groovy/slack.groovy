import com.chetanmeh.oak.index.config.generator.IndexConfigGeneratorHelper
import com.chetanmeh.oak.state.export.NodeStateExporter
import com.google.common.base.Throwables
import groovy.json.JsonOutput

String command = request.getParameter('command')
String query = request.getParameter('text')


def json
def error
try {
    def indexNodeState = IndexConfigGeneratorHelper.getIndexConfig(query)
    json = NodeStateExporter.toJson(indexNodeState)
}catch (Throwable t){
    error = "Invalid " + t.getMessage()
}

def resultText = json ?: error

def result = [
        'response_type' : 'in_channel',
        'text' : 'Suggested index definition',
        'attachments' : [[
                'text' : "```$resultText```",
                'mrkdwn_in' : ['text']
        ]
        ]
]

if (error){
    result.'attachments'[0].'color' = 'danger'
}

response.setHeader('Content-Type', 'application/json')
println JsonOutput.prettyPrint(JsonOutput.toJson(result))