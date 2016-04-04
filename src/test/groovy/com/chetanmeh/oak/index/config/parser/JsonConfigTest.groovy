package com.chetanmeh.oak.index.config.parser

import org.junit.Test


class JsonConfigTest {

    @Test
    public void propertyDefinition() throws Exception{
        def json = '''  {"cqCloudServiceConfig": {
    "jcr:primaryType": "oak:QueryIndexDefinition",
    "propertyNames": ["cq:cloudserviceconfig"],
    "declaringNodeTypes": [
      "rep:DenyACE",
      "rep:GrantACE",
      "rep:ACE"
    ],
    "type": "property",
    "reindex": false,
    "reindexCount": 1
  }}'''
        def indexes = new JsonConfig(json).parse()
        assert indexes.propertyIndexes.size() == 1
        PropertyIndex pi = indexes.propertyIndexes[0]
        assert pi.path == '/oak:index/cqCloudServiceConfig'
        assert pi.@propertyNames == ['cq:cloudserviceconfig']
        assert pi.declaringNodeTypesList as Set == ['rep:DenyACE','rep:GrantACE','rep:ACE'] as Set
        assert !pi.unique
    }

    @Test
    public void luceneDefinitions() throws Exception{
        def json = '''{"cqLastModifiedLucene": {
    "jcr:primaryType": "oak:QueryIndexDefinition",
    "compatVersion": 2,
    "persistence": "file",
    "includedPaths" : ["/etc/map"],
    "path": "crx-quickstart/repository/lucene/cqLastModifiedLucene",
    "type": "lucene",
    "async": "async",
    "reindex": false,
    "reindexCount": 2,
    "indexRules": {
      "jcr:primaryType": "nt:unstructured",
      "cq:Page": {
        "jcr:primaryType": "nt:unstructured",
        "properties": {
          "jcr:primaryType": "nt:unstructured",
          "cq:lastModified": {
            "jcr:primaryType": "nt:unstructured",
            "ordered": true,
            "propertyIndex": true,
            "name": "jcr:content/cq:lastModified",
            "type": "Date"
          },
          "jcr:data": {
            "jcr:primaryType": "nt:unstructured",
            "name": "jcr:data",
            "index": false
          }
        }
      }
    }
  }

}'''
        def indexes = new JsonConfig(json).parse()
        assert indexes.luceneIndexes.size() == 1
        LuceneIndex li = indexes.luceneIndexes[0]
        assert li.path == '/oak:index/cqLastModifiedLucene'
        assert li.compatVersion == 2
        assert li.includedPaths == ['/etc/map']
        assert li.excludedPaths == []
        assert li.rules.size() == 1
        IndexRule r = li.rules[0]
        assert r.type == 'cq:Page'
        assert r.properties.size() == 2
        PropertyDefinition pd = r.getPropDefn('jcr:content/cq:lastModified')
        assert pd.propertyIndex
        assert pd.ordered
        assert pd.index

        pd = r.getPropDefn("jcr:data")
        assert !pd.index
    }
}
