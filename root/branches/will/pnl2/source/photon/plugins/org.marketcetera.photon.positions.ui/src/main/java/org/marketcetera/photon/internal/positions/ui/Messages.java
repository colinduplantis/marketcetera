package org.marketcetera.photon.internal.positions.ui;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;


/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Messages {
	/**
	 * The message provider.
	 */
	static final I18NMessageProvider PROVIDER = new I18NMessageProvider(
			"photon_positions"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

	/*
	 * UI Text
	 */
	static final I18NMessage0P POSITIONS_VIEW_FILTER_LABEL = new I18NMessage0P(
			LOGGER, "positions_view.filter_label"); //$NON-NLS-1$
	static final I18NMessage0P POSITIONS_TABLE_SYMBOL_COLUMN_HEADING = new I18NMessage0P(
			LOGGER, "positions_table.symbol_column_heading"); //$NON-NLS-1$
	static final I18NMessage0P POSITIONS_TABLE_ACCOUNT_COLUMN_HEADING = new I18NMessage0P(
			LOGGER, "positions_table.account_column_heading"); //$NON-NLS-1$
	static final I18NMessage0P POSITIONS_TABLE_TRADER_COLUMN_HEADING = new I18NMessage0P(
			LOGGER, "positions_table.trader_column_heading"); //$NON-NLS-1$
	static final I18NMessage0P POSITIONS_TABLE_POSITION_COLUMN_HEADING = new I18NMessage0P(
			LOGGER, "positions_table.position_column_heading"); //$NON-NLS-1$
	static final I18NMessage0P POSITIONS_TABLE_POSITION_PL_COLUMN_HEADING = new I18NMessage0P(
			LOGGER, "positions_table.position_pl_column_heading"); //$NON-NLS-1$
	static final I18NMessage0P POSITIONS_TABLE_TRADING_PL_COLUMN_HEADING = new I18NMessage0P(
			LOGGER, "positions_table.trading_pl_column_heading"); //$NON-NLS-1$
	static final I18NMessage0P POSITIONS_TABLE_REALIZED_PL_COLUMN_HEADING = new I18NMessage0P(
			LOGGER, "positions_table.realized_pl_column_heading"); //$NON-NLS-1$
	static final I18NMessage0P POSITIONS_TABLE_UNREALIZED_PL_COLUMN_HEADING = new I18NMessage0P(
			LOGGER, "positions_table.unrealized_pl_column_heading"); //$NON-NLS-1$
	static final I18NMessage0P POSITIONS_TABLE_TOTAL_PL_COLUMN_HEADING = new I18NMessage0P(
			LOGGER, "positions_table.total_pl_column_heading"); //$NON-NLS-1$
}
