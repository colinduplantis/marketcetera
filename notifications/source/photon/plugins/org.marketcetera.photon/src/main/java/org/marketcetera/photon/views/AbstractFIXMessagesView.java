package org.marketcetera.photon.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.messagehistory.FIXRegexMatcher;
import org.marketcetera.photon.messagehistory.FIXStringMatcher;
import org.marketcetera.photon.ui.ContextMenuFactory;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.FIXMessageTableFormat;
import org.marketcetera.photon.ui.FIXMessageTableRefresher;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Field;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.impl.matchers.TrueMatcher;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;

/* $License$ */

/**
 * A view for FIX messages that uses a FIXMessageTableFormat and ensures the
 * columns are refreshed when preferences change.
 * 
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public abstract class AbstractFIXMessagesView
        extends MessagesViewBase<MessageHolder>
{
    /**
     * refresher for the view table
     */
    private FIXMessageTableRefresher tableRefresher;
    /**
     * filter matcher editor used to dynamically filter the table contants
     */
    private final FilterMatcherEditor filterMatcherEditor = new FilterMatcherEditor();
    /**
     * default matcher matches all rows
     */
    private static final ca.odell.glazedlists.matchers.Matcher<MessageHolder> DEFAULT_MATCHER = new TrueMatcher<MessageHolder>();
    /**
     * regex determining the search pattern for the view filter
     */
    private static final String FILTER_PATTERN = "([^~=]*)(=|~=)(.*)"; //$NON-NLS-1$
    /**
     * regex pattern object used to split the filter text entered by the user (this is *not* the regex the user enters)
     */
    private final Pattern mFilterPattern;
    /**
     * text contents of the filter widget
     */
    private String mFilterText = ""; //$NON-NLS-1$
    /**
     * Create a new AbstractFIXMessagesView instance.
     */
    public AbstractFIXMessagesView()
    {
        mFilterPattern = Pattern.compile(FILTER_PATTERN);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.MessagesViewBase#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite inParent)
    {
        super.createPartControl(inParent);
        FIXMessageHistory messageHistory = PhotonPlugin.getDefault().getFIXMessageHistory();
        if (messageHistory != null) {
            setInput(messageHistory);
        }
    }
    /**
     * Sets the input object upon which the message view is based.
     *
     * @param inHistory a <code>FIXMessageHistory</code> value containing the messages to display
     */
    public void setInput(FIXMessageHistory inHistory)
    {
        // this list object is used to hold the messages to be displayed 
        FilterList<MessageHolder> extractedList = (FilterList<MessageHolder>)getMessageList(inHistory);
        // register the dynamic filter generator with the list
        extractedList.setMatcherEditor(getFilterMatcherEditor());
        super.setInput(extractedList);
    }   
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.MessagesViewBase#dispose()
     */
    @Override
    public void dispose()
    {
        super.dispose();
        if (tableRefresher != null) {
            tableRefresher.dispose();
            tableRefresher = null;
        }
    }
    /**
     * Gets the unique ID of the view.
     * 
     * @return a <code>String</code> value
     */
    protected abstract String getViewID();
    /**
     * Gets the subset of the given <code>Message</code> history that applies to the
     * implementing subclass.
     *
     * @param inHistory a <code>FIXMessageHistory</code> value
     * @return a <code>FilterList&lt;MessageHolder&gt;</code> value
     */
    protected abstract FilterList<MessageHolder> getMessageList(FIXMessageHistory inHistory);
    /**
     * Gets the current value of the filter widget.
     *
     * @return a <code>String</code> value
     */
    protected final String getFilterText()
    {
        return mFilterText;
    }
    /**
     * Gets the dynamic match generator.
     *
     * @return a <code>FilterMatcherEditor</code> value
     */
    protected final FilterMatcherEditor getFilterMatcherEditor()
    {
        return filterMatcherEditor;
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.photon.views.MessagesViewBase#initializeToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    @Override
    protected void initializeToolBar(IToolBarManager inTheToolBarManager)
    {
        TextContributionItem filterTextContributionItem = new TextContributionItem(""); //$NON-NLS-1$
        filterTextContributionItem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e)
            {
                Text theText = (Text) e.widget;
                setFilterText(theText.getText());
                if ('\r' == e.character) {
                    try {
                        handleFilter(mFilterText);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
        inTheToolBarManager.add(filterTextContributionItem);
    }
    /**
     * The FIXMessageTableFormat manages the addition/removal of columns. The
     * Enum[] columns feature should not be used to create columns.
     * 
     * @return always null
     */
    @Override
    protected Enum<?>[] getEnumValues()
    {
        return null;
    }
    /**
     * Creates the table format for the view.
     *
     * @param inMessageTable a <code>Table</code> value
     * @return a <code>FIXMessageTableFormat&lt;MessageHolder&gt;</code> value
     */
    protected FIXMessageTableFormat<MessageHolder> createFIXMessageTableFormat(Table inMessageTable)
    {
        String viewID = getViewID();
        return new FIXMessageTableFormat<MessageHolder>(inMessageTable,
                                                        viewID,
                                                        MessageHolder.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.MessagesViewBase#createTableViewer(org.eclipse.swt.widgets.Table, java.lang.Enum<?>[])
     */
    @Override
    protected IndexedTableViewer createTableViewer(Table inMessageTable,
                                                   Enum<?>[] inEnums)
    {
        IndexedTableViewer aMessagesViewer = new IndexedTableViewer(inMessageTable);
        getSite().setSelectionProvider(aMessagesViewer);
        aMessagesViewer.setContentProvider(new EventListContentProvider<MessageHolder>());

        FIXMessageTableFormat<MessageHolder> tableFormat = createFIXMessageTableFormat(inMessageTable);
        aMessagesViewer.setLabelProvider(tableFormat);

        tableRefresher = new FIXMessageTableRefresher(aMessagesViewer,
                                                      tableFormat);

        createContextMenu(inMessageTable);

        return aMessagesViewer;
    }
    /**
     * Creates the <code>Matcher</code> to use for the <code>String</code> match filter.
     *
     * @param inFixField an <code>int</code> value containing the FIX field against which to match
     * @param inValue a <code>String</code> value containing the value to match against the table rows
     * @return a <code>ca.odell.glazedlists.matchers.Matcher&lt;MessageHolder&gt;</code> value
     */
    protected ca.odell.glazedlists.matchers.Matcher<MessageHolder> createStringMatcher(int inFixField,
                                                                                       String inValue)
    {
        return new FIXStringMatcher(inFixField,
                                    inValue);
    }
    /**
     * Creates the <code>Matcher</code> to use for the <code>Regex</code> match filter.
     *
     * @param inFixField an <code>int</code> value containing the FIX field against which to match
     * @param inValue a <code>String</code> value containing the value to match against the table rows
     * @return a <code>ca.odell.glazedlists.matchers.Matcher&lt;MessageHolder&gt;</code> value
     */
    protected ca.odell.glazedlists.matchers.Matcher<MessageHolder> createRegexMatcher(int inFixField,
                                                                                      String inValue)
    {
        return new FIXRegexMatcher(inFixField,
                                    inValue);
    }
    /**
     * Processes a change in the contents of the filter widget.
     *
     * @param inValue a <code>String</code> value containing the text of the filter widget
     */
    protected final void handleFilter(String inValue)
    {
        // inValue contains the full text of what the user entered in the filter widget, e.g. Side=B
        if (inValue.length() > 0) {
            // some filter value has been entered, process that value
            Matcher regexMatcher = mFilterPattern.matcher(inValue);
            // if the value is meaningful, continue, otherwise, exit and do nothing (no filtering changes)
            if (regexMatcher.matches()) {
                // the first match of the regex must be the field against which to match
                String fieldSpecifier = regexMatcher.group(1);
                try {
                    // the second is the operator (either '=' or '~=' as dictated by the regex pattern)
                    String operator = regexMatcher.group(2);
                    // the third is the value to match
                    String value = regexMatcher.group(3);
                    // the field name may be one that we've translated to a shorter or more readable version
                    // try to translate it back first
                    String fieldNameToUse = FIXFieldLocalizer.readFIXFieldNameFromCache(fieldSpecifier);
                    // dig out the field object indicated by the fieldSpecified (a failure will throw a CoreException)
                    Field<?> fixField = FIXMessageUtil.getQuickFixFieldFromName(fieldNameToUse);
                    // this is the int value of the FIX field which we now know is valid
                    int fixFieldInt = fixField.getField();
                    // create a matcher depending on the operator entered by the user
                    if ("=".equals(operator)) { //$NON-NLS-1$
                        // use a straight string matcher
                        getFilterMatcherEditor().setMatcher(createStringMatcher(fixFieldInt,
                                                                                value));
                    } else {
                        // use a regex matcher
                        assert "~=".equals(operator); //$NON-NLS-1$
                        getFilterMatcherEditor().setMatcher(createRegexMatcher(fixFieldInt,
                                                                               value));
                    }
                } catch (Throwable t) {
                    PhotonPlugin.getMainConsoleLogger().error(UNRECOGNIZED_FIELD.getText(fieldSpecifier));
                }
            }
        } else {
            // the value entered by the user is of zero length, use the default matcher
            getFilterMatcherEditor().setMatcher(getDefaultMatcher());
        }
    }
    /**
     * Gets the default <code>Matcher</code> to use when no filter is specified.
     *
     * @return a <code>ca.odell.glazedlists.matchers.Matcher&lt;MessageHolder&gt;</code> value
     */
    protected ca.odell.glazedlists.matchers.Matcher<MessageHolder> getDefaultMatcher()
    {
        return DEFAULT_MATCHER;
    }
    /**
     * Sets the filter text contents.
     *
     * @param inText a <code>String</code> value
     */
    private void setFilterText(String inText)
    {
        mFilterText = inText;
    }
    /**
     * Creates the context menu for the given <code>Table</code>.
     *
     * @param inTable a <code>Table</code> value
     */
    private void createContextMenu(Table inTable)
    {
        ContextMenuFactory contextMenuFactory = new ContextMenuFactory();
        contextMenuFactory.createContextMenu("fixMessageContextMenu", //$NON-NLS-1$
                                             inTable,
                                             getSite());
    }
    /**
     * Coordinates application of <code>Matcher</code> objects to create a filter.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")//$NON-NLS-1$
    protected final static class FilterMatcherEditor
        extends AbstractMatcherEditor<MessageHolder>
    {
        /**
         * Sets the <code>Matcher</code> used in the editor.
         *
         * @param inMatcher a <code>ca.odell.glazedlists.matchers.Matcher&lt;MessageHolder&gt;</code> value
         */
        protected final void setMatcher(ca.odell.glazedlists.matchers.Matcher<MessageHolder> inMatcher)
        {
            fireChanged(inMatcher);
        }
    }
}
