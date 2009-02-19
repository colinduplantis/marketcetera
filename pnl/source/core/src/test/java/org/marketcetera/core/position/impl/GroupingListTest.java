package org.marketcetera.core.position.impl;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcher;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcherFactory;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TransactionList;
import ca.odell.glazedlists.event.ListEvent;

/* $License$ */

/**
 * Test {@link GroupingList}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class GroupingListTest {
	GroupMatcherFactory<String, GroupMatcher<String>> factory = new GroupMatcherFactory<String, GroupMatcher<String>>() {

		@Override
		public GroupMatcher<String> createGroupMatcher(final String element) {
			class MyMatcher implements GroupMatcher<String> {
				String key;

				MyMatcher(String key) {
					this.key = key;
				}

				@Override
				public boolean matches(String item) {
					return key.equals(item);
				}

				@Override
				public int compareTo(GroupMatcher<String> o) {
					MyMatcher my = (MyMatcher) o;
					return key.compareTo(my.key);
				}
			}
			;
			return new MyMatcher(element);
		}
	};
	
	abstract class TestTemplate implements Runnable {
		@Override
		public void run() {
			EventList<String> base = GlazedLists.eventListOf();
			TransactionList<String> trans = new TransactionList<String>(base);
			initList(trans);
			
			org.marketcetera.core.position.impl.GroupingList<String> groupingList = new org.marketcetera.core.position.impl.GroupingList<String>(
					trans, factory);
			List<ExpectedListChanges<?>> listeners = new LinkedList<ExpectedListChanges<?>>();
			for (int i = 0; i < groupingList.size(); i++) {
				ExpectedListChanges<String> listChangeListener = new ExpectedListChanges<String>("Group "+Integer.toString(i), getGroupsExpected(i));
					listeners.add(listChangeListener);
					groupingList.get(i).addListEventListener(listChangeListener);
			}

			ExpectedListChanges<EventList<String>> listChangeListener = new ExpectedListChanges<EventList<String>>("Root List", getExpected());
				listeners.add(listChangeListener);
				groupingList.addListEventListener(listChangeListener);
			trans.beginEvent();
			runTransaction(trans);
			trans.commitEvent();		
			for (ExpectedListChanges<?> i : listeners) {
				i.exhausted();
			}
		}

		protected abstract void initList(TransactionList<String> trans);		
		protected int[] getExpected() {
			return new int[]{};
		}
		protected int[] getGroupsExpected(int i) {
			return new int[]{};
		}
		protected abstract void runTransaction(TransactionList<String> trans);

		
	}
	
	
	
	@Test
	public void AB_iAAB() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("B");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(0, "A");
			}
			
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 0};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 0) {
					return new int[] {2, 0};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void AB_AiAB() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("B");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(1, "A");
			}

			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 0};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 0) {
					return new int[] {2, 1};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void BA_ABA() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("B");
				trans.add("A");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(0, "A");
			}

			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 0};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 0) {
					return new int[] {2, 0};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void AB_ABiB() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("B");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(2, "B");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 1};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 1) {
					return new int[] {2, 1};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void ABC_AiBBC() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("B");
				trans.add("C");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(1, "B");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 1};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 1) {
					return new int[] {2, 0};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void ABC_ABiBC() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("B");
				trans.add("C");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(2, "B");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 1};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 1) {
					return new int[] {2, 1};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void B_AB() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("B");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(0, "A");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {2, 0};
			}
		}.run();

	}
	
	@Test
	public void AC_ABC() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("C");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(1, "B");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {2, 1};
			}
		}.run();

	}
	
	@Test
	public void AC_EBAC() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("C");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(0, "B");
				trans.add(0, "E");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.INSERT, 1, ListEvent.INSERT, 3};
			}
		}.run();

	}
	
	@Test
	public void AC_CEBABC() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("C");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.add(0, "B");
				trans.add(0, "C");
				trans.add(3, "B");
				trans.add(1, "E");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.INSERT, 1, ListEvent.UPDATE, 2, ListEvent.INSERT, 3};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 1) {
					return new int[] {ListEvent.INSERT, 0};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void AC_AuC() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("C");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.set(1, "C");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 1};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 1) {
					return new int[] {ListEvent.UPDATE, 0};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void AC_AuB() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("C");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.set(1, "B");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.INSERT, 1, ListEvent.DELETE, 2};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 1) {
					return new int[] {ListEvent.DELETE, 0};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void ABC_ABuB() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("B");
				trans.add("C");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.set(2, "B");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 1, ListEvent.DELETE, 2};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 1) {
					return new int[] {ListEvent.INSERT, 1};
				}
				if (i == 2) {
					return new int[] {ListEvent.DELETE, 0};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void A_dAiA() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.remove(0);
				trans.add(0, "A");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 0};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 0) {
					return new int[] {ListEvent.DELETE, 0, ListEvent.INSERT, 0};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
	
	@Test
	public void temp() {
		TransactionList<String> trans = new TransactionList<String>(GlazedLists.eventListOf("A","B","C"));
		trans.addListEventListener(new ExpectedListChanges<String>("temp", new int[] {1, 1, 1, 2}));
		trans.beginEvent();
		trans.set(1, "B");
		trans.set(2, "C");
		trans.set(1, "B");
		trans.commitEvent();
	}
	
	@Test
	public void ABC_AdBiBiCC() {
		new TestTemplate() {
		
			@Override
			protected void initList(TransactionList<String> trans) {
				trans.add("A");
				trans.add("B");
				trans.add("C");
			}
		
			@Override
			protected void runTransaction(TransactionList<String> trans) {
				trans.remove(1);
				trans.add(1, "C");
				trans.add(1, "B");
			}
		
			@Override
			protected int[] getExpected() {
				return new int[] {ListEvent.UPDATE, 1, ListEvent.UPDATE, 2};
			}

			@Override
			protected int[] getGroupsExpected(int i) {
				if (i == 1) {
					return new int[] {ListEvent.INSERT, 0, ListEvent.DELETE, 1};
				}
				if (i == 2) {
					return new int[] {ListEvent.INSERT, 0};
				}
				return super.getGroupsExpected(i);
			}
		}.run();

	}
}
