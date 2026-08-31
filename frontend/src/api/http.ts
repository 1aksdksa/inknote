import axios, {
  AxiosError,
  type AxiosInstance,
  type AxiosRequestConfig,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from 'axios'
import { toCamelCase, toSnakeCase } from '@/utils/caseConvert'

/** 关闭本次请求的驼峰/下划线互转 */
export const HEADER_DISABLE_CAMEL_CASE = 'disable-camel-case'
/** 仅关闭响应驼峰转换 */
export const HEADER_DISABLE_RESPONSE_CAMEL_CASE = 'disable-response-camel-case'
/** 不拆统一响应体，直接返回原始 data */
export const HEADER_RAW_RESPONSE = 'x-raw-response'
/** 失败时不触发全局错误提示 */
export const HEADER_SILENT_ERROR = 'x-silent-error'

/** 与后端约定：成功码 */
export const SUCCESS_CODE = '0'

export interface ApiResult<T = unknown> {
  code: string | number
  message: string
  data: T
}

export interface HealthResponse {
  status: string
  service: string
  time: number
  database: string
}

export class HttpError extends Error {
  readonly status?: number
  readonly code?: string | number
  readonly raw?: unknown
  readonly silent: boolean

  constructor(
    message: string,
    options?: { status?: number; code?: string | number; raw?: unknown; silent?: boolean },
  ) {
    super(message)
    this.name = 'HttpError'
    this.status = options?.status
    this.code = options?.code
    this.raw = options?.raw
    this.silent = Boolean(options?.silent)
  }
}

export type HttpErrorHandler = (error: HttpError) => void

let globalErrorHandler: HttpErrorHandler | null = null

/** 注册全局错误处理（例如统一 toast） */
export function setHttpErrorHandler(handler: HttpErrorHandler | null) {
  globalErrorHandler = handler
}

function notifyError(error: HttpError) {
  if (error.silent) return
  globalErrorHandler?.(error)
}

function headerFlag(config: InternalAxiosRequestConfig | undefined, name: string): boolean {
  if (!config?.headers) return false
  const value = config.headers[name]
  return value === true || value === 'true' || value === '1'
}

function isApiResult(data: unknown): data is ApiResult {
  if (data === null || typeof data !== 'object' || Array.isArray(data)) return false
  return 'code' in data && 'message' in data && 'data' in data
}

function isSuccessCode(code: string | number): boolean {
  return code === SUCCESS_CODE || code === 0
}

const instance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

function transformRequestBody(data: unknown): unknown {
  if (data == null) return data
  if (typeof FormData !== 'undefined' && data instanceof FormData) return data
  if (typeof Blob !== 'undefined' && data instanceof Blob) return data
  if (typeof data === 'string') return data
  return toSnakeCase(data)
}

function resolveErrorMessage(error: AxiosError<{ message?: string; code?: string | number }>): string {
  if (error.code === 'ECONNABORTED') {
    return '请求超时，请稍后重试'
  }
  if (error.response) {
    return (
      error.response.data?.message ||
      error.message ||
      `请求失败（${error.response.status}）`
    )
  }
  if (error.request) {
    return '网络异常，请检查网络或后端是否已启动'
  }
  return error.message || '请求失败'
}

instance.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('inknote_token')
  if (token) {
    config.headers.set('Authorization', `Bearer ${token}`)
  }
  if (typeof FormData !== 'undefined' && config.data instanceof FormData) {
    config.headers.delete('Content-Type')
  }
  if (!headerFlag(config, HEADER_DISABLE_CAMEL_CASE)) {
    if (config.params && typeof config.params === 'object') {
      config.params = toSnakeCase(config.params)
    }
    if (config.data !== undefined) {
      config.data = transformRequestBody(config.data)
    }
  }
  return config
})

instance.interceptors.response.use(
  (response: AxiosResponse) => {
    const silent = headerFlag(response.config, HEADER_SILENT_ERROR)
    const rawMode = headerFlag(response.config, HEADER_RAW_RESPONSE)

    if (
      !headerFlag(response.config, HEADER_DISABLE_CAMEL_CASE) &&
      !headerFlag(response.config, HEADER_DISABLE_RESPONSE_CAMEL_CASE) &&
      response.data != null
    ) {
      response.data = toCamelCase(response.data)
    }

    // 统一响应体：{ code, message, data }；成功码为 "0"
    if (!rawMode && isApiResult(response.data)) {
      if (!isSuccessCode(response.data.code)) {
        const bizError = new HttpError(response.data.message || '业务处理失败', {
          status: response.status,
          code: response.data.code,
          raw: response.data,
          silent,
        })
        notifyError(bizError)
        if (String(response.data.code) === '101001004') {
          localStorage.removeItem('inknote_token')
          localStorage.removeItem('inknote_profile')
          if (typeof window !== 'undefined' && !window.location.pathname.startsWith('/login')) {
            window.location.assign('/login')
          }
        }
        return Promise.reject(bizError)
      }
      response.data = response.data.data
    }

    return response
  },
  (error: AxiosError<{ message?: string; code?: string | number }>) => {
    const silent = headerFlag(error.config, HEADER_SILENT_ERROR)
    const httpError = new HttpError(resolveErrorMessage(error), {
      status: error.response?.status,
      code: error.response?.data?.code,
      raw: error.response?.data ?? error,
      silent,
    })
    notifyError(httpError)
    return Promise.reject(httpError)
  },
)

export interface RequestConfig extends AxiosRequestConfig {
  /** 不拆统一响应体 */
  raw?: boolean
  /** 失败时不走全局错误提示 */
  silent?: boolean
}

function applyFlags(config: RequestConfig = {}): AxiosRequestConfig {
  const { raw, silent, headers, ...rest } = config
  const nextHeaders: Record<string, string> = { ...(headers as Record<string, string>) }
  if (raw) nextHeaders[HEADER_RAW_RESPONSE] = 'true'
  if (silent) nextHeaders[HEADER_SILENT_ERROR] = 'true'
  return { ...rest, headers: nextHeaders }
}

async function request<T>(config: RequestConfig): Promise<T> {
  const response = await instance.request<T>(applyFlags(config))
  return response.data
}

export const http = {
  instance,
  get<T>(url: string, config?: RequestConfig) {
    return request<T>({ ...config, method: 'GET', url })
  },
  post<T>(url: string, data?: unknown, config?: RequestConfig) {
    return request<T>({ ...config, method: 'POST', url, data })
  },
  put<T>(url: string, data?: unknown, config?: RequestConfig) {
    return request<T>({ ...config, method: 'PUT', url, data })
  },
  delete<T>(url: string, config?: RequestConfig) {
    return request<T>({ ...config, method: 'DELETE', url })
  },
}

export default http
