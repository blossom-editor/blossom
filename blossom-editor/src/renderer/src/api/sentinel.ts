import { defaultRequest as rq } from "./request";
import type { R } from "./request";

/**
 * 流量直方图
 */
export const metricLineApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/sentinel/metric/line", { params });
};