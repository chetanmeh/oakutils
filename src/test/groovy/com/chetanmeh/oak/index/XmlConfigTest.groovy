package com.chetanmeh.oak.index

import org.junit.Test


class XmlConfigTest {


    @Test
    public void propertyDefinition() throws Exception{
        def xml = createXml('''<acPrincipalName
        jcr:primaryType="oak:QueryIndexDefinition"
        declaringNodeTypes="{Name}[rep:DenyACE,rep:GrantACE,rep:ACE]"
        propertyNames="{Name}[rep:principalName]"
        reindex="{Boolean}false"
        reindexCount="{Long}1"
        type="property"/>''')

        def indexes = new XmlConfig(xml).parse()
        assert indexes.propertyIndexes.size() == 1
        PropertyIndex pi = indexes.propertyIndexes[0]
        assert pi.path == '/oak:index/acPrincipalName'
        assert pi.@propertyNames == ['rep:principalName']
        assert pi.declaringNodeTypesList as Set == ['rep:DenyACE','rep:GrantACE','rep:ACE'] as Set
        assert pi.unique == false
    }

    @Test
    public void luceneDefinition() throws Exception{
        def xml = createXml(''' <slingMapping
        jcr:primaryType="oak:QueryIndexDefinition"
        async="async"
        compatVersion="{Long}2"
        evaluatePathRestrictions="{Boolean}true"
        includedPaths="[/etc/map]"
        reindex="{Boolean}false"
        reindexCount="{Long}11"
        type="lucene">
        <indexRules jcr:primaryType="nt:unstructured">
            <sling:Mapping jcr:primaryType="nt:unstructured">
                <properties jcr:primaryType="nt:unstructured">
                    <sling:internalRedirect
                        jcr:primaryType="nt:unstructured"
                        name="sling:internalRedirect"
                        propertyIndex="{Boolean}true"/>
                    <sling:redirect
                        jcr:primaryType="nt:unstructured"
                        name="sling:redirect"
                        ordered="{Boolean}true"
                        propertyIndex="{Boolean}true"/>
                    <ontime
                        jcr:primaryType="nt:unstructured"
                        index="{Boolean}false"
                        name="ontime"/>
                </properties>
            </sling:Mapping>
        </indexRules>
    </slingMapping>''')
        def indexes = new XmlConfig(xml).parse()
        assert indexes.luceneIndexes.size() == 1
        LuceneIndex li = indexes.luceneIndexes[0]
        assert li.path == '/oak:index/slingMapping'
        assert li.compatVersion == 2
        assert li.includedPaths == ['/etc/map']
        assert li.excludedPaths == []
        assert li.rules.size() == 1
        IndexRule r = li.rules[0]
        assert r.type == 'sling:Mapping'
        assert r.properties.size() == 3
        PropertyDefinition pd = r.getPropDefn('sling:internalRedirect')
        assert pd.propertyIndex
        assert !pd.ordered
        assert pd.index

        pd = r.getPropDefn('sling:redirect')
        assert pd.ordered

        pd = r.getPropDefn("ontime")
        assert !pd.index
    }

    @Test
    public void simpleValue() throws Exception{
        assert 'false' == XmlConfig.parseJcrValue('{Boolean}false')
        assert '1' == XmlConfig.parseJcrValue('{Long}1')
    }

    @Test
    public void listValue() throws Exception{
        assert ['containeeInstanceId'] == XmlConfig.parseJcrArray('{Name}[containeeInstanceId]')
        assert ['rep:DenyACE','rep:GrantACE','rep:ACE'] == XmlConfig.parseJcrArray('{Name}[rep:DenyACE,rep:GrantACE,rep:ACE]')
    }

    def createXml(String fragment){
        "$xmlHeader $fragment $xmlFooter"
    }

    static final def xmlHeader = '''<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:oak="http://jackrabbit.apache.org/oak/ns/1.0" xmlns:xmpMM="http://ns.adobe.com/xap/1.0/mm/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:slingevent="http://sling.apache.org/jcr/event/1.0" xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:granite="http://www.adobe.com/jcr/granite/1.0" xmlns:dam="http://www.day.com/dam/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:if="http://www.f-i.de/if/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:mix="http://www.jcp.org/jcr/mix/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0" xmlns:rep="internal"
    jcr:primaryType="nt:unstructured">'''

    static final def xmlFooter = '''</jcr:root>'''

}
