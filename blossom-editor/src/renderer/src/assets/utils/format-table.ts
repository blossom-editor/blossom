/**
 * 格式化 markdown 表格
 * @param str 表格字符串
 * @returns 
 */
export const formartMarkdownTable = (str: string) => {
  var table = splitStringToTable(str),
    alignments: string[],
    max_length_per_column: any[];

  table[1] = table[1].map(function (cell) {
    return padHeaderSeparatorString(cell, 0);
  });

  fillInMissingColumns(table);

  alignments = table[1].map(getAlignment);
  max_length_per_column = getMaxLengthPerColumn(table);

  return table.map(function (row, row_index) {
    return '|' + row.map(function (cell, column_index) {
      var column_length = max_length_per_column[column_index];
      if (row_index === 1) {
        return padHeaderSeparatorString(cell, column_length + 2);
      }
      return ' ' + padStringWithAlignment(cell, column_length, alignments[column_index]) + ' ';
    }).join('|') + '|';
  }).join('\n') + '\n';
}



function splitStringToTable(str: string) {
  return trim(String(str)).split('\n').map(function (row) {
    row = row.replace(/^\s*\|/, '');
    row = row.replace(/\|\s*$/, '');
    return row.split('|').map(trim);
  });
}


function getMaxLengthPerColumn(table: any[]) {
  return table[0].map(function (_str: any, column_index: string | number) {
    return getMaxLengthOfColumn(getColumn(table, column_index));
  });
}


function getMaxLengthOfColumn(array: any[]) {
  return array.reduce(function (max: number, item: any) {
    return Math.max(max, getItemLength(item));
  }, 0);
}


function getItemLength(str: string) {
  var len = 0, i: number;
  for (i = 0; i < str.length; i++) {
    len += (str.charCodeAt(i) >= 0 && str.charCodeAt(i) <= 128) ? 1 : 2;
  }
  return len;
}


function getMaxLength(array: any[]) {
  return array.reduce(function (max: number, item: string | any[]) {
    return Math.max(max, item.length);
  }, 0);
}


function padHeaderSeparatorString(str: string, len: number) {
  switch (getAlignment(str)) {
    case 'l': return repeatStr('-', Math.max(3, len));
    case 'c': return ':' + repeatStr('-', Math.max(3, len - 2)) + ':';
    case 'r': return repeatStr('-', Math.max(3, len - 1)) + ':';
  }
}


function getAlignment(str: string | any[]) {
  if (str[str.length - 1] === ':') {
    return str[0] === ':' ? 'c' : 'r';
  }
  return 'l';
}


function fillInMissingColumns(table: any[]) {
  var max = getMaxLength(table);
  table.forEach(function (row: string[], i: number) {
    while (row.length < max) {
      row.push(i === 1 ? '---' : '');
    }
  });
}


function getColumn(table: any[], column_index: string | number) {
  return table.map(function (row) {
    return row[column_index];
  });
}


function trim(str: string) {
  return str.trim();
}


function padStringWithAlignment(str: string, len: number, alignment: string): string {
  switch (alignment) {
    case 'l': return padRight(str, len);
    case 'c': return padLeftAndRight(str, len);
    case 'r': return padLeft(str, len);
  }
  return ''
}


function padLeft(str: string, len: number): string {
  return getPadding(len - getItemLength(str)) + str;
}


function padRight(str: string, len: number): string {
  return str + getPadding(len - getItemLength(str));
}


function padLeftAndRight(str: string, len: number): string {
  var l = (len - getItemLength(str)) / 2;
  return getPadding(Math.ceil(l)) + str + getPadding(Math.floor(l));
}


function getPadding(len: number): string {
  return repeatStr(' ', len);
}


function repeatStr(str: string, count: number): string {
  return count > 0 ? Array(count + 1).join(str) : '';
}