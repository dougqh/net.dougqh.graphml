package net.dougqh.graphml.test;

import java.io.File;
import java.io.IOException;

import net.dougqh.graphml.GraphmlWriter;
import net.dougqh.graphml.yed.YedLineStyle;
import net.dougqh.graphml.yed.YedShape;

public final class YedBasicsTest {
	public static final void main( final String[] args )
		throws IOException
	{
		File outputDir = new File( args[ 0 ] );
		File outputFile = new File( outputDir, "yed-basics.graphml" );
		
		GraphmlWriter writer = new GraphmlWriter( outputFile ).forYed();
		try {
			writer.startGraphml();
			writer.startGraph();
			
			String alphaId = writer.startNode();
			writer.yed().startShapeNode();
			writer.yed().nodeLabel( "Alpha" );
			writer.yed().borderStyle( YedLineStyle.DOTTED );
			writer.yed().geometry( 50, 50 );
			writer.yed().fill( "#0000ff" );
			writer.yed().shape( YedShape.OCTAGON );
			writer.yed().endShapeNode();
			writer.endNode();
			
			String betaId = writer.startNode();
			writer.yed().startShapeNode();
			writer.yed().nodeLabel( "Beta" );
			writer.yed().endShapeNode();
			writer.endNode();
			
			String gammaId = writer.startNode();
			writer.yed().startShapeNode();
			writer.yed().nodeLabel( "Gamma" );
			writer.yed().endShapeNode();
			writer.endNode();
			
			String deltaId = writer.startNode();
			writer.yed().startShapeNode();
			writer.yed().nodeLabel( "Delta" );
			writer.yed().endShapeNode();
			writer.endNode();
			
			writer.edge( alphaId, betaId );
			writer.edge( alphaId, gammaId );
			writer.edge( alphaId, deltaId );
			
			writer.endGraph();
			writer.endGraphml();
		} finally {
			writer.close();
		}
	}
}
