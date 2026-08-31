import { http } from '@/api/http'

export interface NoteListItem {
  noteId: string
  title: string
  updatedAt: number
}

export interface NotePage {
  current: number
  size: number
  total: number
  records: NoteListItem[]
}

export interface NoteDetail {
  noteId: string
  title: string
  contentMd: string
  createdAt: number
  updatedAt: number
}

export function fetchNotePage(payload: {
  current: number
  size: number
  keyword?: string
}) {
  return http.post<NotePage>('/note/page', payload)
}

export function fetchNoteDetail(noteId: string) {
  return http.post<NoteDetail>('/note/detail', { noteId })
}

export function saveNote(payload: {
  noteId?: string
  title: string
  contentMd: string
}) {
  return http.post<NoteDetail>('/note/save', payload)
}

export function deleteNote(noteId: string) {
  return http.post<void>('/note/delete', { noteId })
}

export interface UploadImageResult {
  url: string
  fileName: string
}

export function uploadNoteImage(file: File) {
  const form = new FormData()
  form.append('file', file)
  return http.post<UploadImageResult>('/file/upload-image', form)
}
