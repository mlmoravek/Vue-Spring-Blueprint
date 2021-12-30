export interface Header {
  /** object property key as field key */
  field: string;
  /** label for the column */
  label: string;
  /** if the column is soratble */
  sortable?: boolean;
  /** if optional the columns isn't shown directy but can be activated */
  optional?: boolean;
  /** display function to edit output */
  display?: (value: any, row: any) => string;
  // all props from buefy table column are also possible
  [key: string]: any;
  // https://buefy.org/documentation/table/#api-view
}

export type ActionFunction = (rows: any[]) => void;

export interface Action {
  /** action title shown in dropdown */
  title: string;
  /** function to be called if the action is fired, with all selected rows as property */
  f: ActionFunction;
}

export interface Options {
  searchable: boolean;
  actions: Action[];
  page: number;
  sort: string;
  sortOrder: string;
  customColumns: boolean;
}

export type LoadFunction = (
  page: number,
  size?: number,
  sort?: string,
  ascending?: boolean,
  search?: string,
  filter?: string,
) => Promise<{ rows: any[]; total: number }>;
