export const isNull = (val: any): boolean => {
  if (typeof val === 'boolean') {
    return false
  }
  if (typeof val === 'number') {
    return false
  }

  // 控制
  if (val == null || val === 'undefined' || val === undefined || val === '') {
    return true
  }

  // 数组
  if (Array.isArray(val) && val.length === 0) {
    return true
  }

  // 对象, 但无字段
  if (val instanceof Object && JSON.stringify(val) === '{}') {
    return true
  }
  return false
}

export const isNotNull = (val: any): boolean => {
  return !isNull(val)
}

export const isBlank = (str: string | undefined | null): boolean => {
  if (str === undefined) {
    return true
  }
  if (str == null) {
    return true
  }
  if (str == '') {
    return true
  }
  if (str == ' ') {
    return true
  }
  if (str.length == 0) {
    return true
  }
  return false
}

export const isNotBlank = (str: string | undefined | null): boolean => {
  return !isBlank(str)
}