<%

    def TEXT_DEFAULT = '''
    //Paste your input text here

    {
      "_id": "2:/oak:index/lucene",
      "_commitRoot": {
        "r14de69eb812-0-1": "0",
        "r14de6a81855-0-1": "0"
      },
      "reindex": {
        "r14de69eb812-0-1": "true",
        "r14de6a81855-0-1": "false"
      },
      "_deleted": {
        "r14de69eb812-0-1": "false"
      },
      "reindexCount": {
        "r14de6a81855-0-1": "1"
      },
      "_lastRev": {
        "r0-0-1": "r14e021c76df-0-1"
      }
    }
    '''
    String timezone = request.getParameter("timezone") ?: "UTC";
    String text = request.getParameter("text") ?: TEXT_DEFAULT;
%>
<html>
<head>
  <title>Oak Utilities : Revision Formatter</title>
  <link type="text/css" rel="stylesheet" href="github-markdown.css"/>
  <link type="text/css" rel="stylesheet" href="stylish.css"/>
</head>
<body>
  <article class="markdown-body">
    <h1>Revision Formatter</h1>
    <p class="center">Extracts the revision string from any text and converts them to
      formatted date.</p>

    <form method="POST">
      <p class="center">
        <textarea name="text" rows="10" cols="30" class="prettyRevText format">${text}</textarea>
      </p>
      <p>
        TimeZone :
        <select name="timezone">
          <% TimeZone.availableIDs.each { tz ->
            def selected = tz == timezone ? 'selected' : ''
          %>
          <option value="${tz}" ${selected}>${tz}</option>
          <% } %>
        </select>
      </p>


      <p class="format">
        <input type="submit" value="Format">
      </p>
    </form>
    <a href="/">Back</a>
    <h3>Formatted Revisions</h3>
    <%
        def rp = new com.chetanmeh.oak.RevisionFormatter(timezone)
        def result = rp.format(text)
    %>
      <p class="center">
      <textarea readonly="readonly" class="prettyRevText">${result.formattedText}</textarea>
      </p>

    <h3>Sorted Revisions</h3>
    <p class="desc">Below is the sorted list of all revisions extracted from provided input</p>
    <p class="center">
      <textarea readonly="readonly" class="prettyRevText">${result.extractedRevisions.join('\n')}</textarea>
    </p>

  </article>
</body>
</html>
