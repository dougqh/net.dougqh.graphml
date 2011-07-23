package net.dougqh.graphml.test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import net.dougqh.graphml.yed.YedBasicNode;
import net.dougqh.graphml.yed.YedGroup;
import net.dougqh.graphml.yed.YedLineStyle;
import net.dougqh.graphml.yed.YedShape;
import net.dougqh.graphml.yed.YedWriter;

public final class ElifYed {
	public static final void main( final String[] args )
		throws IOException
	{
		File outputDir = new File( args[ 0 ] );
		File outputFile = new File( outputDir, "elif.graphml" );
		
		YedWriter writer = new YedWriter( outputFile );
		try {
			YedBasicNode center = new YedBasicNode( "Center" ).
				setColor( Color.MAGENTA ).
				setShape( YedShape.OCTAGON ).
				setDimension( 100, 100 );
						
			writer.add( center );
			
			YedBasicNode diamondPrototype = new YedBasicNode().
				setLineStyle( YedLineStyle.DOTTED ).
				setShape( YedShape.DIAMOND ).
				setDimension( 60, 60 );
			
			YedBasicNode alpha = diamondPrototype.clone( "Alpha" );
			YedBasicNode beta = diamondPrototype.clone( "Beta" );
			YedBasicNode gamma = diamondPrototype.clone( "Gamma" );
			YedBasicNode delta = diamondPrototype.clone( "Delta" );
			YedBasicNode epsilon = diamondPrototype.clone( "Epsilon" );
			epsilon.setColor( Color.CYAN );
			
			writer.add( alpha, beta, gamma, delta, epsilon );
			
			writer.connectWithArrow( center, alpha );
			writer.connectWithArrow( center, beta );
			writer.connectWithArrow( center, gamma );
			writer.connectWithArrow( center, delta );
			writer.connectWithArrow( center, epsilon );
			
			writer.connectWithArrows( alpha, beta, gamma, delta, epsilon, alpha );
			
			YedBasicNode test = new YedBasicNode().
				setColor(Color.ORANGE);
			
			YedBasicNode head = test.clone().
			setShape( YedShape.ELLIPSE ).
			setDimension( 40, 40 );
			
			YedBasicNode body = test.clone().
			setShape( YedShape.TRAPEZOID ).
			setDimension( 100, 100 );
			
			YedBasicNode footL = test.clone().
			setShape( YedShape.RECTANGLE ).
			setDimension( 20, 10 );
			
			YedBasicNode footR = test.clone().
			setShape( YedShape.RECTANGLE ).
			setDimension( 20, 10 );
			
			writer.add( test, head, body, footL, footR );
			
			writer.connect( head, body );
			writer.connect( body, footL );
			writer.connect( body, footR );
			
			writer.connectWithArrow( alpha, test );
		} finally {
			writer.close();
		}
	}
}
