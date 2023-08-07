import { defaultRequest as rq } from "./request";
import type { R } from "./request";

/**
 * 应用集群流量详情
 */
export const metricByAppApi = (params?: object): Promise<R<any>> => {
  // return rq.get<R<any>>("${BSMS_URL}/sentinel/es/metric/app", params);
  return rq.get<R<any>>("${GATEWAY_URL}/${SERVICE_ID}/sentinel/metric/app", { params });
};

/**
 * 资源列表 
 */
export const resourcesApi = (params?: object): Promise<R<any>> => {
  // return rq.get<R<any>>("${BSMS_URL}/sentinel/es/resources", params);
  return rq.get<R<any>>("${GATEWAY_URL}/${SERVICE_ID}/sentinel/resources", { params });
};


/**
 * 流量直方图
 */
export const metricLineApi = (params?: object): Promise<R<any>> => {
  return rq.get<R<any>>("/sentinel/metric/line", { params });
};