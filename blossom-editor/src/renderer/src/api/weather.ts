import { defaultRequest as rq } from "./request";
import type { R } from "./request";

export const getAll = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/weather", {params});
}