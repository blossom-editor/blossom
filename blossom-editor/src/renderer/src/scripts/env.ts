const tryuse = import.meta.env.MODE === 'tryuse'

/**
 * 是否试用
 * @returns
 */
export const isTryuse = () => {
  return tryuse
}
