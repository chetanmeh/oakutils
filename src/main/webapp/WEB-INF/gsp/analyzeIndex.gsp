<%
    com.chetanmeh.oak.index.config.parser.Indexes indexes = com.chetanmeh.oak.index.config.parser.RequestConfigHandler.getIndexInfo(request)
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Oak Utilities : Index Definition Analyzer</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
</head>
<body>
<div class="container">
    <div class="row">
        <h1 class="display-1 text-center">Index Definition Analyzer</h1>
        <% if (indexes) {%>
        <h3>Index Details</h3>
        <ul>
            <li>Index Count - <%= indexes.noOfIndexes() %></li>
            <li><a href="#lucene-index">Lucene Index Count -</a><%= indexes.luceneIndexes.size() %></li>
            <li><a href="#property-index">Property Index Count - </a><%= indexes.propertyIndexes.size() %></li>
            <% if (indexes.nodeTypeIndex) {%>
            <li><a href="#node-type-index">NodeType Index Count - </a><%= indexes.nodeTypeIndex.declaringNodeTypesList.size()%></li>
            <% } %>
            <li>Disabled Index Count - <%= indexes.disabledIndexes.size() %></li>
        </ul>
        <a href="/">Back</a>
    </div>

    <div class="row">
        <h3>Analysis Report</h3>
        <% if(indexes.reportNotEmpty()) {%>
        <ul>
        <% if (indexes.duplicateRules) {%>
            <li>Multiple rules for same type</li>
            <ul>
                <% indexes.duplicateRules.each {k,v ->%>
                <li>$k - $v</li>
                <%}%>
            </ul>
        <%}%>
        <% if (indexes.duplicateProperties) {%>
        <li>Duplicate property definitions - Properties mentioned below <b>might</b> be having duplicate index
        entries. Note that an entry below does not always indicate a problem with config. Its just highlighted
        so that it can be looked into once!</li>
        <ul>
            <% indexes.duplicateProperties.each {k,v ->%>
            <li>$k - $v</li>
            <%}%>
        </ul>
        <%}%>
        </ul>
        <% } else { %>
        All is well!
        <% } %>
    </div>

    <% if (indexes.propertyIndexes) {%>
        <div class="row">
            <a name="property-index"></a>
            <h3>Property Index Details</h3>
            <%
                    int i = 0
                    boolean partition = false
                    String colClass = ""
                    int partitionSize = indexes.propertyIndexes.size()
                    if (partition){
                        partitionSize = indexes.propertyIndexes.size() / 2 + 1
                        colClass = "col-xs-6"
                    }
                    com.google.common.collect.Lists.partition(indexes.propertyIndexes, partitionSize).each { idxs ->
            %>
            <div class="${colClass}">
            <table class="table table-sm table-hover">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Path</th>
                    <th>Property Name</th>
                    <th>Declaring Node Types</th>
                </tr>
                </thead>
                <tbody>
                <%
                    idxs.each {idx ->
                    def u = idx.unique ? "(U)" : ""
                    def reindex =  idx.reindexCount ? "(${idx.reindexCount})" : ""
                    def idxDetail = "${idx.path} $reindex $u"
                    i++
                %>
                <tr>
                    <th scope="row">$i</th>
                    <td>${idxDetail}</td>
                    <td>${idx.propertyNames}</td>
                    <td>${idx.declaringNodeTypes}</td>
                </tr>
                <% } %>
                </tbody>
            </table>
            </div>
            <% } %>
        </div>
    <% } %>

    <% if (indexes.luceneIndexes) {%>
        <div class="row">
        <a name="lucene-index"></a>
        <h3>Lucene Index Details</h3>
        <table class="table table-bordered table-sm table-hover">
        <% int i = 0;
            int colCount = 7
            boolean includeFullTextAttrs = false
            if (includeFullTextAttrs){
                colCount = 12
            }
            indexes.luceneIndexes.each {li ->
            i++
        %>
            <tr>
            <th colspan="${colCount}" bgcolor="#f5f5f5">$i ${li.path}</th>
            </tr>
            <tr>
                <td colspan="${colCount}">
                <ul>
                    <% if (li.excludedPaths) {%><li>Excluded Path ${li.excludedPaths}</li><%}%>
                    <% if (li.includedPaths) {%><li>Included Path ${li.includedPaths}</li><%}%>
                </ul>
                </td>
            </tr>
            <% li.rules.each {rule -> %>
                <tr>
                    <td colspan="${colCount}"><strong>${rule.type}</strong></td>
                </tr>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Property Index</th>
                    <th>Index</th>
                    <th>Ordered</th>
                    <th>Analyzed</th>
                    <th>Null Check</th>
                    <% if (includeFullTextAttrs) { %>
                    <th>Excerpt</th>
                    <th>Node Scope Index</th>
                    <th>Suggest</th>
                    <th>Spell Check</th>
                    <th>Facets</th>
                    <% } %>
                </tr>
                <% int pdCount = 0;rule.properties.each {pd -> pdCount++%>
                <tr>
                    <th scope="row">$pdCount</th>
                    <td>${pd.name}</td>
                    <td>${pd.propertyIndex ? "&checkmark;" : ""}</td>
                    <td>${pd.index ? "&checkmark;" : "&#10007;"}</td>
                    <td>${pd.ordered ? "&checkmark;" : ""}</td>
                    <td>${pd.analyzed ? "&checkmark;" : ""}</td>
                    <td>${pd.nullCheckEnabled ? "&checkmark;" : ""}</td>
                    <% if (includeFullTextAttrs) { %>
                    <td>${pd.useInExcerpt ? "&checkmark;" : ""}</td>
                    <td>${pd.nodeScopeIndex ? "&checkmark;" : ""}</td>
                    <td>${pd.useInSuggest ? "&checkmark;" : ""}</td>
                    <td>${pd.useInSpellcheck ? "&checkmark;" : ""}</td>
                    <td>${pd.facets ? "&checkmark;" : ""}</td>
                    <% } %>
                </tr>
            <% } %>
            <% } %>
            <% } %>
        </table>
    </div>
    <% } %>

    <% if (indexes.nodeTypeIndex) {%>
        <div class="row">
            <a name="node-type-index"></a>
            <h3>NodeType Index Details</h3>
            <%
                    int i = 0
                    boolean partition = true
                    String colClass = ""
                    def list = indexes.nodeTypeIndex.declaringNodeTypesList
                    int partitionSize = list.size()
                    if (partition){
                        partitionSize = list.size() / 3 + 1
                        colClass = "col-md-4"
                    }
                    com.google.common.collect.Lists.partition(list, partitionSize).each { idxs ->
            %>
            <div class="${colClass}">
                <table class="table table-sm">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Node Types</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                            idxs.each {nt -> i++
                    %>
                    <tr>
                        <th scope="row">$i</th>
                        <td>$nt</td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>
        <% } %>
    <% } else {%>
    <p class="lead">Analyzes the index definitions. Index definitions can be provided in either xml or json format</p>
    <ul>
        <li>xml - Upload the <code>.content.xml</code> from exported index definition content package</li>
        <li>json - Upload the json from http://host:port/oak:index.tidy.-1.json</li>
        <li>zip - Content package zip file having the index content at <i>/oak:index/.content.xml</i></li>
    </ul>

    <form method="POST" enctype="multipart/form-data">
        <label class="file">
            <input type="file" id="file" name="indexConfig">
            <span class="file-custom"></span>
            <small class="text-muted">xml, zip or json file containing exported index config</small>
        </label>
        <input type="submit" value="Analyze">
    </form>
    <a href="/">Back</a>
    </div>
    <% } %>
</body>
</html>
