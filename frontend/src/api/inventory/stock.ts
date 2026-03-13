import request from '@/utils/request'

// 查询库存列表
export function listStock(query: any) {
  return request({
    url: '/inventory/stock/list',
    method: 'get',
    params: query
  })
}

// 查询库存预警列表
export function listWarningStock(query: any) {
  return request({
    url: '/inventory/stock/warning',
    method: 'get',
    params: query
  })
}

// 查询库存详情
export function getStock(stockId: number) {
  return request({
    url: '/inventory/stock/' + stockId,
    method: 'get'
  })
}

// 调整库存
export function updateStock(data: any) {
  return request({
    url: '/inventory/stock',
    method: 'put',
    data: data
  })
}
