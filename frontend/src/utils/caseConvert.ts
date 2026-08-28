function isPlainObject(value: unknown): value is Record<string, unknown> {
  if (value === null || typeof value !== 'object') return false
  if (Array.isArray(value)) return false
  if (value instanceof Date || value instanceof Blob || value instanceof File || value instanceof FormData) {
    return false
  }
  const proto = Object.getPrototypeOf(value)
  return proto === Object.prototype || proto === null
}

function toCamelKey(key: string): string {
  return key.replace(/_([a-zA-Z])/g, (_, char: string) => char.toUpperCase())
}

function toSnakeKey(key: string): string {
  return key.replace(/[A-Z]/g, (char) => `_${char.toLowerCase()}`)
}

/** 下划线 → 驼峰（响应体） */
export function toCamelCase<T>(input: T): T {
  if (Array.isArray(input)) {
    return input.map((item) => toCamelCase(item)) as T
  }
  if (!isPlainObject(input)) {
    return input
  }
  const result: Record<string, unknown> = {}
  for (const [key, value] of Object.entries(input)) {
    result[toCamelKey(key)] = toCamelCase(value)
  }
  return result as T
}

/** 驼峰 → 下划线（请求体 / query） */
export function toSnakeCase<T>(input: T): T {
  if (Array.isArray(input)) {
    return input.map((item) => toSnakeCase(item)) as T
  }
  if (!isPlainObject(input)) {
    return input
  }
  const result: Record<string, unknown> = {}
  for (const [key, value] of Object.entries(input)) {
    result[toSnakeKey(key)] = toSnakeCase(value)
  }
  return result as T
}
