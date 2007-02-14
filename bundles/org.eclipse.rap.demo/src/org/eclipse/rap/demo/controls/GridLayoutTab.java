/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;

class GridLayoutTab extends ExampleTab {

  private boolean propPrefSize;
  private boolean propEqualWidth;

  public GridLayoutTab( final TabFolder folder ) {
    super( folder, "GridLayout" );
  }

  protected void createStyleControls() {
    final Button prefSizeButton = createPropertyButton( "Preferred Size" );
    prefSizeButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        propPrefSize = prefSizeButton.getSelection();
        createNew();
      }
    } );
    final Button equalButton = createPropertyButton( "Make Columns Equal Width" );
    equalButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        propEqualWidth = equalButton.getSelection();
        createNew();
      }
    } );
  }

  protected void createExampleControls( final Composite parent ) {
    GridLayout parentLayout = new GridLayout();
    parentLayout.marginWidth = 5;
    parent.setLayout( parentLayout );

    Composite comp = new Composite( parent, RWT.NONE );
    comp.setBackground( Color.getColor( 0xcc, 0xb7, 0x91 ) );
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    gridLayout.marginWidth = 3;
    gridLayout.marginHeight = 3;
    gridLayout.makeColumnsEqualWidth = propEqualWidth;
    comp.setLayout( gridLayout );

    Button button1 = new Button(comp, RWT.PUSH);
    button1.setText("B1");
    GridData gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    button1.setLayoutData(gridData);

    new Button(comp, RWT.PUSH).setText( "Wide Button 2" );

    Button button3 = new Button( comp, RWT.PUSH );
    button3.setText("Button 3");
    gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    gridData.verticalSpan = 2;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    button3.setLayoutData(gridData);

    Button button4 = new Button( comp, RWT.PUSH);
    button4.setText("B4");
    gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    button4.setLayoutData(gridData);

    new Button( comp, RWT.PUSH).setText("Button 5");    

    if( propPrefSize ) {
      comp.setLayoutData( new GridData() );
    } else {
      comp.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    }
    comp.layout();
    registerControl( comp );
  }
}
