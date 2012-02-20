/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.theme.css.CssElementHolder;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


public class Theme {

  private static final String JS_THEME_PREFIX = "org.eclipse.swt.theme.";

  private final String id;
  private final String jsId;
  private final String name;
  private StyleSheetBuilder styleSheetBuilder;
  private ThemeCssValuesMap valuesMap;

  private String registeredLocation;

  private IThemeCssElement[] elements;

  public Theme( String id, String name, StyleSheet styleSheet ) {
    if( id == null ) {
      throw new NullPointerException( "id" );
    }
    this.id = id;
    this.name = name != null ? name : "Unnamed Theme";
    jsId = createUniqueJsId( id );
    valuesMap = null;
    styleSheetBuilder = new StyleSheetBuilder();
    if( styleSheet != null ) {
      styleSheetBuilder.addStyleSheet( styleSheet );
    }
  }

  public String getId() {
    return id;
  }

  public String getJsId() {
    return jsId;
  }

  public String getName() {
    return name;
  }

  public void addStyleSheet( StyleSheet styleSheet ) {
    if( valuesMap != null ) {
      throw new IllegalStateException( "Theme is already initialized" );
    }
    styleSheetBuilder.addStyleSheet( styleSheet );
  }

  public void initialize( ThemeableWidget[] themeableWidgets ) {
    elements = extractElements( themeableWidgets );
    if( valuesMap != null ) {
      throw new IllegalStateException( "Theme is already initialized" );
    }
    StyleSheet styleSheet = styleSheetBuilder.getStyleSheet();
    valuesMap = new ThemeCssValuesMap( this, styleSheet, themeableWidgets );
    styleSheetBuilder = null;
  }

  private IThemeCssElement[] extractElements( ThemeableWidget[] themeableWidgets ) {
    CssElementHolder elements = new CssElementHolder();
    for( ThemeableWidget themeableWidget : themeableWidgets ) {
      if( themeableWidget.elements != null ) {
        for( IThemeCssElement element : themeableWidget.elements ) {
          elements.addElement( element );
        }
      }
    }
    return elements.getAllElements();
  }

  public StyleSheet getStyleSheet() {
    return styleSheetBuilder.getStyleSheet();
  }

  public ThemeCssValuesMap getValuesMap() {
    if( valuesMap == null ) {
      throw new IllegalStateException( "Theme is not initialized" );
    }
    return valuesMap;
  }

  public String getRegisteredLocation() {
    return registeredLocation;
  }

  public void registerResources( IResourceManager resourceManager ) {
    try {
      registerThemeResources( resourceManager );
      registerThemeStoreFile( resourceManager );
    } catch( IOException ioe ) {
      throw new ThemeManagerException( "Failed to register theme resources for theme " + id, ioe );
    }
  }

  private void registerThemeResources( IResourceManager resourceManager ) throws IOException {
    QxType[] values = valuesMap.getAllValues();
    for( QxType value : values ) {
      if( value instanceof ThemeResource ) {
        registerResource( resourceManager, ( ThemeResource )value );
      }
    }
  }

  private void registerThemeStoreFile( IResourceManager resourceManager ) {
    ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
    storeWriter.addTheme( this, id == ThemeManager.FALLBACK_THEME_ID );
    String name = "rap-" + jsId + ".js";
    String code = storeWriter.createJs();
    registeredLocation = registerUtf8Resource( resourceManager, name, code );
  }

  private static void registerResource( IResourceManager resourceManager, ThemeResource value )
    throws IOException
  {
    String registerPath = value.getResourcePath();
    if( registerPath != null ) {
      InputStream inputStream = value.getResourceAsStream();
      if( inputStream == null ) {
        throw new IllegalArgumentException( "Resource not found for theme property: " + value );
      }
      try {
        resourceManager.register( registerPath, inputStream );
      } finally {
        inputStream.close();
      }
    }
  }

  private static String registerUtf8Resource( IResourceManager resourceManager,
                                              String name,
                                              String content )
  {
    byte[] buffer;
    try {
      buffer = content.getBytes( "UTF-8" );
    } catch( UnsupportedEncodingException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
    ByteArrayInputStream inputStream = new ByteArrayInputStream( buffer );
    RegisterOptions options = RegisterOptions.VERSION_AND_COMPRESS;
    resourceManager.register( name, inputStream, "UTF-8", options );
    return resourceManager.getLocation( name );
  }

  private static String createUniqueJsId( String id ) {
    String result;
    if( RWT.DEFAULT_THEME_ID.equals( id ) ) {
      result = JS_THEME_PREFIX + "Default";
    } else if( ThemeManager.FALLBACK_THEME_ID.equals( id ) ) {
      result = JS_THEME_PREFIX + "Fallback";
    } else {
      String hash = Integer.toHexString( id.hashCode() );
      result = JS_THEME_PREFIX + "Custom_" + hash;
    }
    return result;
  }
}
