import request from '@/utils/request'

// 查询采购单列表
export function listPurchase(query: any) {
  return request({
    url: '/inventory/purchase/list',
    method: 'get',
    params: query
  })
}

// 查询采购单详情
export function getPurchase(purchaseId: number) {
  return request({
    url: '/inventory/purchase/' + purchaseId,
    method: 'get'
  })
}

// 创建采购单
export function createPurchase(data: any) {
  return request({
    url: '/inventory/purchase',
    method: 'post',
    data: data
  })
}

// 确认入库
export function confirmPurchase(purchaseId: number) {
  return request({
    url: '/inventory/purchase/confirm/' + purchaseId,
    method: 'put'
  })
}

// 取消采购单
export function cancelPurchase(purchaseId: number) {
  return request({
    url: '/inventory/purchase/cancel/' + purchaseId,
    method: 'put'
  })
}

// 删除采购单
export function delPurchase(purchaseIds: number | number[]) {
  return request({
    url: '/inventory/purchase/' + purchaseIds,
    method: 'delete'
  })
}
