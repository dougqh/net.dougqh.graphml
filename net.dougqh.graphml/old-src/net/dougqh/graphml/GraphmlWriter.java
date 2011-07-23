package net.dougqh.graphml;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


public final class GraphmlWriter 
    implements Closeable, Flushable
{
    public static final String EXTENSION = ".graphml";
    
    private static final String VERSION = "1.0";
    private static final String ENCODING = "UTF-8";
    
    private static final XMLOutputFactory FACTORY = XMLOutputFactory.newInstance();
    
    private final OutputStream out;
    private final boolean close;
    private final XMLStreamWriter xmlWriter;
    
    private final Map< Object, String > graphIds = new HashMap< Object, String >( 8 );
    private final Map< Object, String > nodeIds = new HashMap< Object, String >( 32 );
    
    private final List< Edge > edges = new ArrayList< Edge >( 32 );
    
    private int curGraphId = 0;
    private int curNodeId = 0;
    
    private YedGraphmlExtension yed = null;

    public GraphmlWriter( final File file ) 
        throws GraphmlIoException, FileNotFoundException
    {
        this( new FileOutputStream( file ), true );
    }
    
    public GraphmlWriter( final OutputStream out )
        throws GraphmlIoException
    {
        this( out, false );
    }
    
    public GraphmlWriter( final OutputStream out, final boolean close )
        throws GraphmlIoException
    {
        this.out = out;
        this.close = close;
        try {
            this.xmlWriter = FACTORY.createXMLStreamWriter( out, ENCODING );
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
    }
    
    public final GraphmlWriter forYed() {
        this.yed = new YedGraphmlExtension( this );
        return this;
    }
    
    public final YedGraphmlExtension yed() {
        if ( this.yed != null ) {
            return this.yed;
        } else {
            throw new IllegalStateException( "yEd support was not enabled" );
        }
    }
    
    public final void startGraphml()
        throws GraphmlIoException
    {
        this.startDocument( ENCODING, VERSION );
        
        this.start( "graphml" ).
            attrib( "xmlns", "http://graphml.graphdrawing.org/xmlns" ).
            attrib( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" ).
            attrib( "xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd" );
        
        if ( this.yed != null ) {
            this.yed.addNamespaces();
        }
        
        if ( this.yed != null ) {
            this.yed.addMetaInfo();
        }
    }
    
    public final void startKey(
    	final String id,
    	final GraphmlElement elementType )
    	throws GraphmlIoException
    {
        this.start( "key" ).attrib( "id", id ).attrib( "for", elementType.getId() );
    }
    
    public final void endKey() throws GraphmlIoException {
        this.end();
    }
    
    public final String startDirectedGraph(
    	final Object object )
    	throws GraphmlIoException
    {
    	return this.startGraph(
    		object,
    		EdgeStyle.DIRECTED );
    }
    
    public final String startDirectedGraph()
    	throws GraphmlIoException
    {
    	return this.startGraph( EdgeStyle.DIRECTED );
    }
    
    public final String startUndirectedGraph(
    	final Object object )
    	throws GraphmlIoException
    {
    	return this.startGraph(
    		object,
    		EdgeStyle.UNDIRECTED );
    }
    
    public final String startUndirectedGraph()
    	throws GraphmlIoException
    {
    	return this.startGraph( EdgeStyle.UNDIRECTED );
    }
    
    public final String startGraph(
    	final Object object,
    	final EdgeStyle edgeStyle )
        throws GraphmlIoException
    {
        String id = this.startGraph( edgeStyle );
        this.graphIds.put( object, id );
        return id;
    }
    
    public final String startGraph(
    	final EdgeStyle edgeStyle )
        throws GraphmlIoException
    {
        String graphId = this.nextGraphId();
        this.start( "graph" ).
            attrib( "id", graphId ).
            attrib( "edgedefault", edgeStyle.getId() );
        return graphId;
    }    
    
    public final String startNode( final Object node ) throws GraphmlIoException {
        String id = this.startNode();
        this.nodeIds.put( node, id );
        return id;
    }
    
    public final String startNode() throws GraphmlIoException {
        String nodeId = this.nextNodeId();
        this.start( "node" ).attrib( "id", nodeId );
        return nodeId;
    }
    
    public final void startData( final String key ) throws GraphmlIoException {
        this.start( "data" ).attrib( "key", key );
    }
    
    public final void endData() throws GraphmlIoException {
        this.end();
    }
    
    public final void port( final String name ) throws GraphmlIoException {
    	this.start( "port" ).attrib( "name", name ).end();
    }
    
    public final void endNode() throws GraphmlIoException {
        this.end();
    }
    
    public final void edge(
        final Object sourceNode,
        final Object targetNode )
        throws GraphmlIoException
    {
        this.edge(
            this.nodeIds.get( sourceNode ),
            this.nodeIds.get( targetNode ) );
    }
    
    public final void edge(
    	final Object sourceNode,
    	final Object targetNode,
    	final EdgeStyle edgeStyle )
    	throws GraphmlIoException
    {
    	this.edge(
    		this.nodeIds.get( sourceNode ),
    		this.nodeIds.get( targetNode ),
    		null );
    }
    
    public final void edge(
    	final String sourceId,
    	final String targetId )
        throws GraphmlIoException
    {
        this.edge( sourceId, targetId, null );
    }
    
    public final void edge(
    	final String sourceId,
    	final String targetId,
    	final EdgeStyle edgeStyle )
        throws GraphmlIoException
    {
        this.edge(
        	sourceId, null,
        	targetId, null,
        	edgeStyle );
    }

    public final void directedEdge(
    	final Object sourceNode,
    	final Object targetNode )
    	throws GraphmlIoException
    {
    	this.directedEdge(
    		this.nodeIds.get( sourceNode ),
    		this.nodeIds.get( targetNode ) );
    }
    
    public final void directedEdge(
    	final String sourceId,
    	final String targetId )
        throws GraphmlIoException
    {
        this.edge(
        	sourceId, null,
        	targetId, null,
        	EdgeStyle.DIRECTED );
    }
    
    public final void undirectedEdge(
    	final Object sourceNode,
    	final Object targetNode )
    	throws GraphmlIoException
    {
    	this.undirectedEdge(
    		this.nodeIds.get( sourceNode ),
    		this.nodeIds.get( targetNode ) );
    }
    
    public final void undirectedEdge(
    	final String sourceId,
    	final String targetId )
        throws GraphmlIoException
    {
    	this.edge(
    		sourceId, null,
    		targetId, null,
    		EdgeStyle.UNDIRECTED );
    }
    
    public final void edge(
        final Object sourceNode,
        final String sourcePort,
        final Object targetNode,
        final String targetPort )
        throws GraphmlIoException
    {
        this.edge(
            this.nodeIds.get( sourceNode ), sourcePort,
            this.nodeIds.get( targetNode ), targetPort );
    }
    
    public final void edge(
    	final Object sourceNode,
    	final String sourcePort,
    	final Object targetNode,
    	final String targetPort,
    	final EdgeStyle edgeStyle )
    	throws GraphmlIoException
    {
    	this.edge(
    		this.nodeIds.get( sourceNode ), sourcePort,
    		this.nodeIds.get( targetNode ), targetPort,
    		null );
    }
    
    public final void edge(
    	final String sourceId,
    	final String sourcePort,
    	final String targetId,
    	final String targetPort )
        throws GraphmlIoException
    {
        this.edge(
        	sourceId, sourcePort,
        	targetId, targetPort,
        	null );
    }
    
    public final void edge(
    	final String sourceId,
    	final String sourcePort,
    	final String targetId,
    	final String targetPort,
    	final EdgeStyle edgeStyle )
        throws GraphmlIoException
    {
        this.edges.add(
        	new Edge(
        		sourceId, sourcePort,
        		targetId, targetPort,
        		edgeStyle ) );
    }

    public final void directedEdge(
    	final Object sourceNode,
    	final String sourcePort,
    	final Object targetNode,
    	final String targetPort )
    	throws GraphmlIoException
    {
    	this.directedEdge(
    		this.nodeIds.get( sourceNode ), sourcePort,
    		this.nodeIds.get( targetNode ), targetPort );
    }
    
    public final void directedEdge(
    	final String sourceId,
    	final String sourcePort,
    	final String targetId,
    	final String targetPort )
        throws GraphmlIoException
    {
        this.edge(
        	sourceId, sourcePort,
        	targetId, targetPort,
        	EdgeStyle.DIRECTED );
    }
    
    public final void undirectedEdge(
    	final Object sourceNode,
    	final String sourcePort,
    	final Object targetNode,
    	final String targetPort )
    	throws GraphmlIoException
    {
    	this.undirectedEdge(
    		this.nodeIds.get( sourceNode ), sourcePort,
    		this.nodeIds.get( targetNode ), targetPort );
    }
    
    public final void undirectedEdge(
    	final String sourceId,
    	final String sourcePort,
    	final String targetId,
    	final String targetPort )
        throws GraphmlIoException
    {
        this.edge(
        	sourceId, sourcePort,
        	targetId, targetPort,
        	EdgeStyle.UNDIRECTED );
    }
    
    private final void edge( final Edge edge ) throws GraphmlIoException {
    	//DQH - GraphML Spec indicates that "sourcePort" should be "sourceport"
    	//and "targetPort" should be "targetport", but that does not work in
    	//yEd.
        this.start( "edge" );
        this.attrib( "source", edge.sourceId );
        if ( edge.sourcePort != null ) {
        	this.attrib( "sourcePort", edge.sourcePort );
        }
        this.attrib( "target", edge.targetId );
        if ( edge.targetPort != null ) {
        	this.attrib( "targetPort", edge.targetPort );
        }
        if ( edge.edgeStyle != null ) {
        	this.attrib( "directed", edge.edgeStyle.isDirected() );
        }
        this.end();
    }
    
    public final void endGraph() throws GraphmlIoException {
        this.end();
    }    
    
    public final void endGraphml() throws GraphmlIoException {
        for ( Edge edge : this.edges ) {
            this.edge( edge );
        }
        this.edges.clear();
        
        this.end();
    }
    
    private final GraphmlWriter startDocument(
        final String encoding,
        final String version )
        throws GraphmlIoException    
    {
        try {
            this.xmlWriter.writeStartDocument( encoding, version );
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    final GraphmlWriter start( final String element )
        throws GraphmlIoException
    {
        try {
            this.xmlWriter.writeStartElement( element );
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    final GraphmlWriter start(
        final String prefix,
        final String element,
        final String namespaceUri )
        throws GraphmlIoException
    {
        try {
            this.xmlWriter.writeStartElement( prefix, element, namespaceUri );
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    final GraphmlWriter characters( final String characters )
        throws GraphmlIoException
    {
        try {
            this.xmlWriter.writeCharacters( characters );
            return this;
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
    }
    
    final GraphmlWriter attrib( final String name, final Object value )
        throws GraphmlIoException
    {
        try {
            if ( value instanceof Boolean ) {
                if ( (Boolean)value ) {
                    this.xmlWriter.writeAttribute( name, "true" );
                } else {
                    this.xmlWriter.writeAttribute( name, "false" );
                }
            } else {
                this.xmlWriter.writeAttribute( name, value.toString() );
            }
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    final GraphmlWriter end()
        throws GraphmlIoException
    {
        try {
            this.xmlWriter.writeEndElement();
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
        return this;
    }
    
    private final String nextGraphId() {
        return "G" + ( this.curGraphId++ );
    }
    
    private final String nextNodeId() {
        return "N" + ( this.curNodeId++ );
    }
    
    @Override
    public final void flush() throws GraphmlIoException {
        try {
            this.xmlWriter.flush();
        } catch ( XMLStreamException e ) {
            throw new GraphmlIoException( e );
        }
    }
    
    @Override
    public void close() throws GraphmlIoException {
        if ( this.close ) {
            try {
                this.xmlWriter.close();
            } catch ( XMLStreamException e ) {
                throw new GraphmlIoException( e );
            } finally {
            	try {
            		this.out.close();
            	} catch ( IOException e ) {
            		throw new GraphmlIoException( e );
            	}
            }
        }
    }
    
    static final class Edge  {
        final String sourceId;
        final String sourcePort;
        final String targetId;
        final String targetPort;
        final EdgeStyle edgeStyle;
        
        Edge(
        	final String sourceId,
        	final String sourcePort,
        	final String targetId,
        	final String targetPort,
        	final EdgeStyle edgeStyle ) 
        {
            this.sourceId = sourceId;
            this.sourcePort = sourcePort;
            this.targetId = targetId;
            this.targetPort = targetPort;
            this.edgeStyle = edgeStyle;
        }
    }
}
