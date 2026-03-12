import request from '@/utils/request'

// 查询商品列表
export function listGoods(query: any) {
  return request({
    url: '/inventory/goods/list',
    method: 'get',
    params: query
  })
}

// 查询商品详情
export function getGoods(goodsId: number) {
  return request({
    url: '/inventory/goods/' + goodsId,
    method: 'get'
  })
}

// 新增商品
export function addGoods(data: any) {
  return request({
    url: '/inventory/goods',
    method: 'post',
    data: data
  })
}

// 修改商品
export function updateGoods(data: any) {
  return request({
    url: '/inventory/goods',
    method: 'put',
    data: data
  })
}

// 删除商品
export function delGoods(goodsId: number | number[]) {
  return request({
    url: '/inventory/goods/' + goodsId,
    method: 'delete'
  })
}

// 导出商品
export function exportGoods(query: any) {
  return request({
    url: '/inventory/goods/export',
    method: 'post',
    params: query
  })
}
