import { defaultRequest as rq } from "./request";
import type { R } from "./request";

export const webAllApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/web/list", { params });
}

export const webSaveApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/web/save", data);
}

export const webDelApi = (data?: object): Promise<R<any>> => {
  return rq.post<R<any>>("/web/del", data);
}