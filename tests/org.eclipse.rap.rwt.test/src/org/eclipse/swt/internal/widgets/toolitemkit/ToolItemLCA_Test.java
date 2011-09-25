/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ToolItemLCA_Test extends TestCase {
  
  private Display display;
  private Shell shell;
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCheckPreserveValues() {
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( toolBar, SWT.CHECK );
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( ToolItemLCAUtil.PROP_SELECTION ) );
    Fixture.clearPreserved();
    item.setSelection( true );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( ToolItemLCAUtil.PROP_SELECTION ) );
    testPreserveValues( display, item );
  }

  public void testDropDownPreserveValues() {
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.DROP_DOWN );
    Fixture.markInitialized( display );
    testPreserveValues( display, item );
  }

  public void testPushPreserveValues() {
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.PUSH );
    Fixture.markInitialized( display );
    testPreserveValues( display, item );
  }

  public void testRadioPreserveValues() {
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.RADIO );
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( ToolItemLCAUtil.PROP_SELECTION ) );
    Fixture.clearPreserved();
    item.setSelection( true );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( ToolItemLCAUtil.PROP_SELECTION ) );
    testPreserveValues( display, item );
  }

  public void testCheckItemSelected() {
    final boolean[] wasEventFired = { false };
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    final ToolItem item = new ToolItem( toolBar, SWT.CHECK );
    item.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( item, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, item.getSelection() );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String toolItemId = WidgetUtil.getId( item );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( toolItemId + ".selection", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, toolItemId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( true, wasEventFired[ 0 ] );
  }

  public void testRadioItemSelected() {
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );
    ToolItem item0 = new ToolItem( toolBar, SWT.RADIO );
    item0.setSelection( true );
    ToolItem item1 = new ToolItem( toolBar, SWT.RADIO );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String item0Id = WidgetUtil.getId( item0 );
    String item1Id = WidgetUtil.getId( item1 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( item1Id + ".selection", "true" );
    Fixture.fakeRequestParam( item0Id + ".selection", "false" );
    Fixture.readDataAndProcessAction( display );
    assertFalse( item0.getSelection() );
    assertTrue( item1.getSelection() );
  }

  public void testRenderChanges() throws IOException {
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    final ToolItem item = new ToolItem( tb, SWT.CHECK );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( item );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    ToolItemLCA itemLCA = new ToolItemLCA();
    item.setText( "benny" );
    itemLCA.renderChanges( item );
    String expected = "setText( \"benny\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    item.setSelection( true );
    itemLCA.renderChanges( item );
    assertTrue( Fixture.getAllMarkup().endsWith( "setSelection( true );" ) );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    itemLCA.renderChanges( item );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testReadData() {
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( toolBar, SWT.CHECK );
    String itemId = WidgetUtil.getId( item );
    // read changed selection
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "true" );
    WidgetUtil.getLCA( item ).readData( item );
    assertEquals( Boolean.TRUE, Boolean.valueOf( item.getSelection() ) );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "false" );
    WidgetUtil.getLCA( item ).readData( item );
    assertEquals( Boolean.FALSE, Boolean.valueOf( item.getSelection() ) );
  }

  public void testGetImage() {
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( toolBar, SWT.CHECK );

    Image enabledImage = Graphics.getImage( Fixture.IMAGE1 );
    Image disabledImage = Graphics.getImage( Fixture.IMAGE2 );
    assertNull( ToolItemLCAUtil.getImage( item ) );

    item.setImage( enabledImage );
    assertSame( enabledImage, ToolItemLCAUtil.getImage( item ) );

    item.setImage( enabledImage );
    item.setDisabledImage( disabledImage );
    assertSame( enabledImage, ToolItemLCAUtil.getImage( item ) );

    item.setEnabled( false );
    assertSame( disabledImage, ToolItemLCAUtil.getImage( item ) );

    item.setDisabledImage( null );
    assertSame( enabledImage, ToolItemLCAUtil.getImage( item ) );
  }

  private void testPreserveValues( Display display, ToolItem item ) {
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    assertEquals( "", adapter.getPreserved( "toolTipText" ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    item.addSelectionListener( selectionListener );
    item.setText( "some text" );
    item.setEnabled( false );
    item.setToolTipText( "tooltip text" );
    ToolBar toolbar = item.getParent();
    Menu contextMenu = new Menu( toolbar );
    toolbar.setMenu( contextMenu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    if( ( item.getStyle() & SWT.SEPARATOR ) == 0 ) {
      assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    }
    assertEquals( Boolean.TRUE, hasListeners );
    assertEquals( "tooltip text", adapter.getPreserved( "toolTipText" ) );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    assertEquals( contextMenu, adapter.getPreserved( Props.MENU ) );
  }
  
  public void testRenderNoRadioGroupForRadioToolItem() throws Exception {
    ToolBar toolBar = new ToolBar( shell, SWT.NO_RADIO_GROUP );
    ToolItem radioItem = new ToolItem( toolBar, SWT.RADIO );
    Fixture.fakeResponseWriter();
    RadioToolItemLCA radioLCA = new RadioToolItemLCA();
    radioLCA.renderInitialization( radioItem );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.setNoRadioGroup( true );" ) != -1 );
  }
}