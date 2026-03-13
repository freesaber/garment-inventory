import request from '@/utils/request'

// 查询分类列表
export function listCategory(query: any) {
  return request({
    url: '/inventory/category/list',
    method: 'get',
    params: query
  })
}

// 查询分类详情
export function getCategory(categoryId: number) {
  return request({
    url: '/inventory/category/' + categoryId,
    method: 'get'
  })
}

// 查询分类下拉树结构
export function treeselect() {
  return request({
    url: '/inventory/category/treeselect',
    method: 'get'
  })
}

// 查询分类列表（扁平）
export function listCategoryAll() {
  return request({
    url: '/inventory/category/listAll',
    method: 'get'
  })
}

// 新增分类
export function addCategory(data: any) {
  return request({
    url: '/inventory/category',
    method: 'post',
    data: data
  })
}

// 修改分类
export function updateCategory(data: any) {
  return request({
    url: '/inventory/category',
    method: 'put',
    data: data
  })
}

// 删除分类
export function delCategory(categoryId: number) {
  return request({
    url: '/inventory/category/' + categoryId,
    method: 'delete'
  })
}
