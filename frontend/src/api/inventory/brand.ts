import request from '@/utils/request'

// 查询品牌列表
export function listBrand(query: any) {
  return request({
    url: '/inventory/brand/list',
    method: 'get',
    params: query
  })
}

// 查询品牌详情
export function getBrand(brandId: number) {
  return request({
    url: '/inventory/brand/' + brandId,
    method: 'get'
  })
}

// 查询所有品牌（不分页）
export function listBrandAll() {
  return request({
    url: '/inventory/brand/listAll',
    method: 'get'
  })
}

// 新增品牌
export function addBrand(data: any) {
  return request({
    url: '/inventory/brand',
    method: 'post',
    data: data
  })
}

// 修改品牌
export function updateBrand(data: any) {
  return request({
    url: '/inventory/brand',
    method: 'put',
    data: data
  })
}

// 删除品牌
export function delBrand(brandId: number) {
  return request({
    url: '/inventory/brand/' + brandId,
    method: 'delete'
  })
}
