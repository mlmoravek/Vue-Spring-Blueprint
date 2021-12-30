<template>
  <article class="buefy-table">
    <div class="table-nav level">
      <div class="level-left">
        <b-field grouped>
          <b-field
            class="table-action"
            v-if="options.actions != null && options.actions.length > 0"
          >
            <div class="control">
              <b-button @click="doAction(selectedAction, checkedRows)">
                Go
              </b-button>
            </div>
            <b-select v-model="selectedAction" placeholder="Action">
              <option
                v-for="(action, index) in options.actions"
                :key="index"
                :value="action"
              >
                {{ action.title }}
              </option>
            </b-select>
          </b-field>
          <slot name="func" />
        </b-field>
      </div>

      <div class="level-right">
        <b-dropdown
          :triggers="['hover', 'click']"
          aria-role="list"
          position="is-bottom-left"
          class="mr-1"
        >
          <template #trigger>
            <b-button
              icon-left="format-columns"
              aria-label="Spaltenkonfiguration"
              class="has-text-primary"
            />
          </template>
          <b-dropdown-item
            class="has-text-weight-bold mb-1"
            aria-role="listitem"
          >
            Angezeigte Spalten:
          </b-dropdown-item>
          <b-dropdown-item
            v-for="h of header"
            :key="h.field"
            class="field"
            style="min-width: 17rem"
            aria-role="listitem"
            :custom="true"
          >
            <b-switch v-model="columnVisibility[h.field]">
              {{ h.label }}
            </b-switch>
          </b-dropdown-item>
        </b-dropdown>
        <!-- <b-button icon-left="filter" class="mr-1" /> -->
        <Search
          v-if="options.searchable"
          :value="search"
          class="table-search"
          @search="onSearch"
        />
      </div>
    </div>
    <b-table
      v-bind="$attrs"
      v-on="$listeners"
      ref="table"
      class="icon-primary"
      :data="content.rows"
      :columns="options.customColumns ? [] : columns"
      :loading="isLoading"
      :checked-rows.sync="checkedRows"
      :custom-is-checked="(a, b) => a.id == b.id"
      custom-row-key="id"
      focusable
      hoverable
      striped
      :checkable="
        (options.actions != null && options.actions.length > 0) || checkable
      "
      :detailed="detailed"
      detail-transition="fade"
      detail-key="id"
      :show-detail-icon="showDetailIcon"
      :row-class="rowClassFunction"
      :paginated="paginated"
      backend-pagination
      :total="content.total"
      :per-page="paginated ? page.size : content.total"
      :current-page="page.size < content.rows.length ? 1 : currentPage"
      backend-sorting
      :default-sort="[sort.key, sort.dir]"
      @page-change="onPageChange"
      @sort="onSort"
      @click="onClick"
    >
      <!-- Pagination Slot -->
      <template slot="pagination">
        <div class="top level">
          <div class="level-left">
            <b-field horizontal label="Einträge pro Seite" class="table-size">
              <b-select
                name="size"
                v-model="page.size"
                aria-label="Einträge pro Seite"
              >
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50">50</option>
                <option value="100">100</option>
              </b-select>
            </b-field>
          </div>

          <div class="level-right">
            <div class="level-item">
              <b-pagination
                class="pagination"
                type="is-primary"
                :per-page="page.size"
                paginated
                aria-next-label="Nächste Seite"
                aria-previous-label="Vorherige Seite"
                aria-page-label="Seite"
                aria-current-label="Aktuelle Seite"
                :total="content.total"
                :current.sync="currentPage"
                @change="onPageChange"
              />
            </div>
          </div>
        </div>
      </template>
      <!-- Default Slot -->
      <template v-if="options.customColumns">
        <slot>
          <slot name="before" />
          <b-table-column
            v-for="column in columns"
            :key="column.field"
            :field="column.field"
            :label="column.label"
            :sortable="column.sortable"
            :visible="column.visible"
            custom-key="id"
            v-slot="{ row }"
          >
            {{ row[column.field] }}
          </b-table-column>
          <slot name="after" />
        </slot>
      </template>
      <!-- Detail Slot -->
      <template v-slot:detail="{ row, index }">
        <slot name="detail" :row="row.__dataObject" :index="index" />
      </template>
      <!-- Footer Slot -->
      <template #footer>
        <slot name="footer" />
      </template>
      <!-- Empty Slot -->
      <template #empty>
        <slot name="empty" v-if="!isLoading">
          <section class="section">
            <div class="content has-text-grey has-text-centered">
              <p>
                <b-icon icon="frown" size="is-large" />
              </p>
              <p>Leider keine Informationen gefunden...</p>
            </div>
          </section>
        </slot>
      </template>
    </b-table>
  </article>
</template>

<script lang="ts">
import Vue, { PropType } from "vue";
/** See Buefy documentation https://buefy.org/documentation/table/ */
import Search from "@/components/table/TableSearch.vue";
import {
  Action,
  Options,
  Header,
  LoadFunction,
} from "@/components/table/Table";

/**
 * @Emits rowClick event if an row is clicked
 * @Emits checkedRows if an row check change
 * @Emits visibilityChange if the visibilty for a row change
 * @Emits search if the search is triggered
 * @Emits sort if the sort is triggered
 * @Slot default default table slot
 * @Slot func for further functionality in the nav bar
 * @Slot footer table footer slot
 *
 * Also all props and events will be passed to the buefy-table component,
 * so all props and events of the table component can be used.
 */
export default Vue.extend({
  name: "PaginatedTableWrapper",
  components: { Search },
  props: {
    /** define the columns with this header property. If not given buefy uses the inline b-table-column variation. */
    header: {
      type: Array as () => Array<Header>,
    },
    /** options for the table */
    options: {
      type: Object as () => Options,
      default: () => ({} as Options),
    },
    /** shows a checkbox for every row */
    checkable: {
      type: Boolean,
      default: false,
    },
    /** if no data prop is set, this load function is called every time the data should be updated. */
    load: {
      type: Function as PropType<LoadFunction>,
    },
    /** as alternativ to the async load function, a sync data object can be set. */
    data: {
      type: Array,
    },
    /** dis/enable the paginations bar */
    paginated: {
      type: Boolean,
      default: true,
    },
    /** dis/enable the detail slot */
    detailed: {
      type: Boolean,
      default: false,
    },
    /** override for show-detail-icon from buefy table.
     if false the click event will trigger open detail */
    showDetailIcon: {
      type: Boolean,
      default: true,
    },
    /** adds class is-clickable to any row */
    clickable: {
      type: Boolean,
      default: false,
    },
    /** override for row-class from buefy table */
    rowClass: {
      type: Function,
    },
    /** define the unique primary key field for the row element */
    rowKey: {
      type: String,
      default: "id",
    },
    // and all props from buefy table possible
  },
  data: function () {
    return {
      isLoading: false,
      currentPage: 1,
      page: {
        current: this.options?.page || 0,
        size: 10,
      },
      sort: {
        key: this.options?.sort || "",
        dir: this.options?.sortOrder || "asc",
      },
      content: {
        rows: Array<any>(),
        total: 0,
      },
      search: "",
      checkedRows: [],
      selectedAction: {} as Action,
      columnVisibility: {} as { [column: string]: boolean },
    };
  },
  watch: {
    options() {
      this.loadContent();
    },
    data: {
      handler() {
        this.loadContent();
      },
      deep: true,
    },
    page: {
      handler() {
        this.loadContent();
      },
      deep: true,
    },
    sort: {
      handler() {
        this.loadContent();
      },
      deep: true,
    },
    checkedRows(rows) {
      this.$emit("checkedRows", rows);
    },
    header() {
      this.setColumnVisibility();
    },
    columnVisibility: {
      handler() {
        // emit visibility change event
        this.$emit("visibilityChange", this.columnVisibility);
        // set costum column visabiltiy based on columnVisibility
        this.setCustomColumnVisability();
      },
      deep: true,
    },
  },
  computed: {
    columns(): Header[] {
      // filter the columns in header if they should shown
      return this.header.map((h: Header) => {
        const visible = this.columnVisibility[h.field];
        h.visible = visible === undefined ? !h.optional : visible;
        return h;
      });
    },
  },
  mounted() {
    // instanziate columnVisibility object
    this.setColumnVisibility();
    // emit first load
    this.loadContent();
  },
  methods: {
    // do load call
    loadContent() {
      if (this.data) {
        // load content from data prop
        this.content.rows = this.data.map(this.formatRow);
        this.content.total = this.data.length;
      } else if (this.load) {
        // else call load function to fetch data async
        this.isLoading = true;
        this.load(
          this.page.current,
          this.page.size,
          this.sort.key,
          this.sort.dir == "asc",
          this.search,
        )
          .then((data: { rows: any[]; total: number }) => {
            if (!data || !data.rows) return;
            // transforms cells via display property in header settings
            this.content.rows = data.rows.map(this.formatRow);
            this.content.total = this.paginated ? data.total : data.rows.length;
          })
          .finally(() => (this.isLoading = false));
      }
    },
    /** create tableRow object and transforms cells via display property in header settings */
    formatRow(row: any): any {
      const tableRow: any = {
        __dataObject: row,
        id: row[this.rowKey],
      };
      for (const header of this.header) {
        if (header.field.includes(".")) {
          if (header.display) {
            console.warn(
              "display is currently not supported for nested fields",
            );
          }
          const baseField = header.field.replace(/\..*$/, "");
          tableRow[baseField] = row[baseField];
        } else
          tableRow[header.field] = header.display
            ? header.display(row[header.field], row)
            : row[header.field];
      }
      return tableRow;
    },
    /** set the columnVisibility object based on the header property */
    setColumnVisibility() {
      this.columnVisibility = this.header.reduce(
        (obj, h) => ({ ...obj, [h.field]: !h.optional }),
        {},
      );
      this.setCustomColumnVisability();
    },
    /** set the visability for custom columns based on header visability if activated */
    setCustomColumnVisability() {
      // check defaultSlot b-table-columns and set the visablity there if options.customColumns is activated
      if (this.options?.customColumns) {
        const defaultSlots = this.$slots.default || [];
        defaultSlots.forEach((bcolumn: any) => {
          const comp = bcolumn.child;

          // check if b-table-column is included in visible columns
          const visible = this.columns.some(
            (column: any) => comp.field && column.field == comp.field,
          );
          // and always show columns which are no listed in header
          const notInHeader = this.header.some(
            (h: Header) => h.field != comp.field,
          );
          // set b-table-column visible property
          if (notInHeader && comp.visible == undefined) comp.visible = true;
          else if (!notInHeader) comp.visible = visible;
        });
      }
    },
    /** emit click event */
    onClick(obj: any) {
      // open detail if detailed is activated and the detail icon isn't shown
      if (this.detailed && !this.showDetailIcon)
        (this.$refs.table as any).toggleDetails(obj);

      // emit click event
      this.$emit("rowClick", obj.__dataObject);
    },
    /** handle search value change event */
    onSearch(searchValue: string) {
      this.page.current = 0;
      this.search = searchValue;
      this.$emit("search", this.search);
      this.loadContent();
    },
    /** Handle page-change event */
    onPageChange(pageNumber: number) {
      this.page.current = --pageNumber;
    },
    /** Handle sort event */
    onSort(field: any, order: any) {
      this.sort.dir = order;
      this.sort.key = field;
      this.$emit("sort", this.sort);
    },
    /** @unused  check if slot exists */
    hasSlot(slot: string) {
      console.log(this.$slots);
      return !!this.$slots[slot];
    },
    /** handle do action event */
    doAction(action: Action, rows: any[]): void {
      action.f(rows);
    },
    /** the row-class function to add classed to any row from buefy table */
    rowClassFunction(row: any, index: number): String {
      var classStr = "";
      if ((this.detailed && !this.showDetailIcon) || this.clickable)
        classStr += "is-clickable ";
      if (this.rowClass) classStr += this.rowClass(row, index);
      return classStr;
    },
  },
});
</script>

<style scope lang="scss">
.buefy-table {
  width: 100%;
}
.table-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1em;
}

.table-size {
  label {
    font-size: 1.3em;
    margin-right: -5px;
    white-space: nowrap;
  }
}
.table-nav .level-left .field:not(:first-child) {
  margin-bottom: 0.75rem;
  margin-left: 0.75rem;
}

@media (max-width: 768px) {
  .table-nav {
    .level-left {
      display: flex;
    }
    .field {
      margin-bottom: 0 !important;
    }
  }
}

// fix missing color style for pagination
.pagination .pagination-link.is-current {
  background-color: $primary !important;
  border-color: $primary !important;
}
.select::after {
  border-color: $primary !important;
}
</style>
