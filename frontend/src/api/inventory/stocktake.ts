import request from '@/utils/request'

// 查询盘点单列表
export function listStocktake(query: any) {
  return request({
    url: '/inventory/stocktake/list',
    method: 'get',
    params: query
  })
}

// 查询盘点单详情
export function getStocktake(stocktakeId: number) {
  return request({
    url: '/inventory/stocktake/' + stocktakeId,
    method: 'get'
  })
}

// 创建盘点单
export function createStocktake(data: any) {
  return request({
    url: '/inventory/stocktake',
    method: 'post',
    data: data
  })
}

// 审核盘点单
export function auditStocktake(stocktakeId: number) {
  return request({
    url: '/inventory/stocktake/audit/' + stocktakeId,
    method: 'put'
  })
}

// 取消盘点单
export function cancelStocktake(stocktakeId: number) {
  return request({
    url: '/inventory/stocktake/cancel/' + stocktakeId,
    method: 'put'
  })
}

// 删除盘点单
export function delStocktake(stocktakeIds: number | number[]) {
  return request({
    url: '/inventory/stocktake/' + stocktakeIds,
    method: 'delete'
  })
}
