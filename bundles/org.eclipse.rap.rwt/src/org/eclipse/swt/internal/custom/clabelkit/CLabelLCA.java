/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.custom.clabelkit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.theme.IThemeAdapter;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Widget;

public final class CLabelLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.CLabel";

  static final String PROP_TEXT = "text";
  static final String PROP_IMAGE = "image";
  static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_LEFT_MARGIN = "leftMargin";
  static final String PROP_TOP_MARGIN = "topMargin";
  static final String PROP_RIGHT_MARGIN = "rightMargin";
  static final String PROP_BOTTOM_MARGIN = "bottomMargin";

  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );

  public void preserveValues( Widget widget ) {
    CLabel label = ( CLabel )widget;
    ControlLCAUtil.preserveValues( label );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    adapter.preserve( PROP_TEXT, label.getText() );
    adapter.preserve( PROP_IMAGE, label.getImage() );
    adapter.preserve( PROP_ALIGNMENT, new Integer( label.getAlignment() ) );
    adapter.preserve( PROP_LEFT_MARGIN, new Integer( label.getLeftMargin() ) );
    adapter.preserve( PROP_TOP_MARGIN, new Integer( label.getTopMargin() ) );
    adapter.preserve( PROP_RIGHT_MARGIN, new Integer( label.getRightMargin() ) );
    adapter.preserve( PROP_BOTTOM_MARGIN, new Integer( label.getBottomMargin() ) );
    WidgetLCAUtil.preserveCustomVariant( label );
    WidgetLCAUtil.preserveBackgroundGradient( label );
  }

  public void readData( Widget widget ) {
    CLabel label = ( CLabel )widget;
    ControlLCAUtil.processMouseEvents( label );
    ControlLCAUtil.processKeyEvents( label );
    ControlLCAUtil.processMenuDetect( label );
    WidgetLCAUtil.processHelp( label );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    CLabel clabel = ( CLabel )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( clabel );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( clabel.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( clabel ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    CLabel clabel = ( CLabel )widget;
    ControlLCAUtil.renderChanges( clabel );
    WidgetLCAUtil.renderCustomVariant( clabel );
    renderText( clabel );
    renderImage( clabel );
    renderAlignment( clabel );
    renderMargins( clabel );
    WidgetLCAUtil.renderBackgroundGradient( clabel );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  ////////////////////////////////////
  // Helping methods to set properties

  private static void renderText( CLabel clabel ) {
    String newValue = clabel.getText();
    if( WidgetLCAUtil.hasChanged( clabel, PROP_TEXT, newValue, null ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( clabel );
      clientObject.setProperty( "text", newValue );
    }
  }

  private static void renderImage( CLabel clabel ) {
    Image newValue = clabel.getImage();
    if( WidgetLCAUtil.hasChanged( clabel, PROP_IMAGE, newValue, null ) ) {
      Object[] args = null;
      if( newValue != null ) {
        String imagePath = ImageFactory.getImagePath( newValue );
        Rectangle bounds = newValue.getBounds();
        args = new Object[] {
          imagePath,
          new Integer( bounds.width ),
          new Integer( bounds.height )
        };
      }
      IClientObject clientObject = ClientObjectFactory.getForWidget( clabel );
      clientObject.setProperty( "image", args );
    }
  }

  private static void renderAlignment( CLabel clabel ) {
    Integer newValue = new Integer( clabel.getAlignment() );
    if( WidgetLCAUtil.hasChanged( clabel, PROP_ALIGNMENT, newValue, DEFAULT_ALIGNMENT ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( clabel );
      clientObject.setProperty( PROP_ALIGNMENT, getAlignment( clabel.getAlignment() ) );
    }
  }

  private static void renderMargins( CLabel clabel ) {
    Rectangle padding = getThemeAdapter( clabel ).getPadding( clabel );
    renderSingleMargin( clabel, PROP_LEFT_MARGIN, clabel.getLeftMargin(), padding.x );
    renderSingleMargin( clabel, PROP_TOP_MARGIN, clabel.getTopMargin(), padding.y );
    int defRightMargin = padding.width - padding.x;
    renderSingleMargin( clabel, PROP_RIGHT_MARGIN, clabel.getRightMargin(), defRightMargin );
    int defBottomMargin = padding.height - padding.y;
    renderSingleMargin( clabel, PROP_BOTTOM_MARGIN, clabel.getBottomMargin(), defBottomMargin );
  }

  //////////////////
  // Helping methods

  private static String getAlignment( int alignment ) {
    String result;
    if( ( alignment & SWT.LEFT ) != 0 ) {
      result = "left";
    } else if( ( alignment & SWT.CENTER ) != 0 ) {
      result = "center";
    } else if( ( alignment & SWT.RIGHT ) != 0 ) {
      result = "right";
    } else {
      result = "left";
    }
    return result;
  }

  private static void renderSingleMargin( CLabel clabel,
                                          String property,
                                          int margin,
                                          int defaultMargin )
  {

    Integer newValue = new Integer( margin );
    Integer defValue = new Integer( defaultMargin );
    if( WidgetLCAUtil.hasChanged( clabel, property, newValue , defValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( clabel );
      clientObject.setProperty( property, newValue );
    }
  }

  private static CLabelThemeAdapter getThemeAdapter( CLabel clabel ) {
    Object adapter = clabel.getAdapter( IThemeAdapter.class );
    return ( CLabelThemeAdapter )adapter;
  }
}