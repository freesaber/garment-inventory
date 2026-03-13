import request from '@/utils/request'

// 查询所有启用的规格类型
export function listAllSpec() {
  return request({
    url: '/inventory/spec/listAll',
    method: 'get'
  })
}

// 按规格编码查询规格值（颜色/尺码）
export function listSpecValuesByCode(specCode: string) {
  return request({
    url: '/inventory/spec/value/listByCode',
    method: 'get',
    params: { specCode }
  })
}

// 按规格ID查询规格值
export function listSpecValues(specId: number) {
  return request({
    url: '/inventory/spec/value/list',
    method: 'get',
    params: { specId }
  })
}
