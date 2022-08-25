package io.inoa.fleet.ui.internal;

import java.util.List;

import lombok.Getter;

@SuppressWarnings("hiding")
@Getter
public abstract class AbstractListModel<T> {

	final List<T> entries;
	final int pageSize;
	final int pageNumber;
	final int pageCount;
	final boolean previousPage;
	final boolean nextPage;
	final String search;
	final Sort sort;

	public AbstractListModel(
			int pageNumber,
			int pageSize,
			long totalSize,
			List<T> entries,
			String search,
			String sort) {
		this.search = search;
		this.entries = entries == null ? List.of() : entries;
		this.sort = new Sort(sort);
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.pageCount = (int) totalSize / pageSize + ((int) totalSize % pageSize == 0 ? 0 : 1);
		this.previousPage = pageNumber > 1;
		this.nextPage = pageNumber < pageCount;
	}

	@Getter
	public class Sort {

		final String sort;
		final String property;
		final boolean asc;

		public Sort(String sort) {
			this.sort = sort;
			this.property = sort.split(",")[0];
			this.asc = "asc".equals(sort.split(",")[1]);
		}

		public boolean is(String property) {
			return this.property.equals(property);
		}

		public String get(String property) {
			return property + "," + (is(property) && asc ? "desc" : "asc");
		}
	}
}
