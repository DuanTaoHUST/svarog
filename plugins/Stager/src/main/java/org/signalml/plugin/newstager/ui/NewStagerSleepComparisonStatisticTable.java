/* NewStagerSleepComparisonStatisticTable.java created 2008-03-03
 *
 */
package org.signalml.plugin.newstager.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import org.signalml.app.view.TablePopupMenuProvider;

/**
 * NewStagerSleepComparisonStatisticTable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerSleepComparisonStatisticTable extends JTable {

	private static final long serialVersionUID = 1L;

	private TablePopupMenuProvider popupMenuProvider;

	public NewStagerSleepComparisonStatisticTable(
		NewStagerSleepComparisonStatisticTableModel model) {
		super(model, (TableColumnModel) null);

		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)
				&& (e.getClickCount() == 1)) {
					int index = rowAtPoint(e.getPoint());
					ListSelectionModel selectionModel = getSelectionModel();
					if (!selectionModel.isSelectedIndex(index)) {
						selectionModel.setSelectionInterval(index, index);
					}
				}
			}

		});

		getTableHeader().setReorderingAllowed(false);

		TableColumnModel columnModel = getColumnModel();
		columnModel.setColumnSelectionAllowed(false);

	}

	@Override
	public JPopupMenu getComponentPopupMenu() {
		if (popupMenuProvider == null) {
			return null;
		}
		return popupMenuProvider.getPopupMenu(-1, getSelectedRow());
	}

	public TablePopupMenuProvider getPopupMenuProvider() {
		return popupMenuProvider;
	}

	public void setPopupMenuProvider(TablePopupMenuProvider popupMenuProvider) {
		this.popupMenuProvider = popupMenuProvider;
	}

}
