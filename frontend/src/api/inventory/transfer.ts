import request from '@/utils/request'

// 查询调拨单列表
export function listTransfer(query: any) {
  return request({
    url: '/inventory/transfer/list',
    method: 'get',
    params: query
  })
}

// 查询调拨单详情
export function getTransfer(transferId: number) {
  return request({
    url: '/inventory/transfer/' + transferId,
    method: 'get'
  })
}

// 创建调拨单
export function createTransfer(data: any) {
  return request({
    url: '/inventory/transfer',
    method: 'post',
    data: data
  })
}

// 审核调拨单
export function auditTransfer(transferId: number) {
  return request({
    url: '/inventory/transfer/audit/' + transferId,
    method: 'put'
  })
}

// 出库
export function outTransfer(transferId: number) {
  return request({
    url: '/inventory/transfer/out/' + transferId,
    method: 'put'
  })
}

// 入库
export function inTransfer(transferId: number) {
  return request({
    url: '/inventory/transfer/in/' + transferId,
    method: 'put'
  })
}

// 取消调拨单
export function cancelTransfer(transferId: number) {
  return request({
    url: '/inventory/transfer/cancel/' + transferId,
    method: 'put'
  })
}

// 删除调拨单
export function delTransfer(transferIds: number | number[]) {
  return request({
    url: '/inventory/transfer/' + transferIds,
    method: 'delete'
  })
}
