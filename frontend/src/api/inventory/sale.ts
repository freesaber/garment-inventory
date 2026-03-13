import request from '@/utils/request'

// 查询销售单列表
export function listSale(query: any) {
  return request({
    url: '/inventory/sale/list',
    method: 'get',
    params: query
  })
}

// 查询销售单详情
export function getSale(saleId: number) {
  return request({
    url: '/inventory/sale/' + saleId,
    method: 'get'
  })
}

// 创建销售单（开单）
export function createSale(data: any) {
  return request({
    url: '/inventory/sale',
    method: 'post',
    data: data
  })
}

// 删除销售单
export function delSale(saleIds: number | number[]) {
  return request({
    url: '/inventory/sale/' + saleIds,
    method: 'delete'
  })
}
