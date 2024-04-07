/* ======================================================================
 * 文章树状列表
 * ====================================================================== */
import Node from 'element-plus/es/components/tree/src/model/node'
import { DragEvents } from 'element-plus/es/components/tree/src/model/useDragNode'
import { NodeDropType } from 'element-plus/es/components/tree/src/tree.type'
import { Ref } from 'vue'

export const getColor = (node: Node) => {
  if (node.level === 1) {
    return '#878787AF'
  }
  if (node.level === 2) {
    return '#89A319AA'
  }
  if (node.level === 3) {
    return '#A37E19AA'
  }
  if (node.level === 4) {
    return '#A33B19AA'
  }
  if (node.level === 5) {
    return '#19A383AA'
  }
  return
}

export interface NeedUpd {
  i: string
  p: string
  s: number
  n: string
  ty: DocType
}

/**
 * 拖拽后处理各个节点排序
 * @param drag 拖拽的节点
 * @param enter 放入的节点
 * @param dropType 拖拽的类型
 * @param _event 事件
 * @param DocTreeRef 树状列表对象
 * @param docTreeData 树状类表数据
 * @param folderType 文件夹类型: 1:文章文件夹|2:图片文件夹
 * @param updateFn 修改后的回调方法
 */
export const handleTreeDrop = (
  drag: Node,
  enter: Node,
  dropType: NodeDropType,
  _event: DragEvents,
  DocTreeRef: Ref,
  docTreeData: Ref<DocTree[]>,
  folderType: FolderType,
  updateFn: (needUpd: NeedUpd[]) => void
) => {
  // 是否同级别
  const isSame = drag.data.p === enter.data.p
  const dragSourceSort = drag.data.s
  const enterSourceSort = enter.data.s
  // 记录最终需要修改的数据
  const needUpd: NeedUpd[] = []

  // 保存需要传入后台修改的节点
  const addUpd = (node: Node) => {
    needUpd.push({
      i: node.data.i,
      p: node.data.p,
      s: node.data.s,
      n: node.data.n,
      ty: node.data.ty
    })
  }

  console.log(`same: ${isSame}, dropType: ${dropType}, drag-srot: ${dragSourceSort}, enter-srot: ${enterSourceSort}`)
  if (isSame) {
    // drag 在 enter 前
    if (dropType === 'before') {
      // 下方的拖动到上方
      if (dragSourceSort > enterSourceSort) {
        drag.data.s = enterSourceSort
        addUpd(drag)
        for (let i = 0; i < enter.parent.childNodes.length; i++) {
          const node = enter.parent.childNodes[i]
          if (checkFolderType(node, folderType) && node.data.s >= enterSourceSort && node.data.s < dragSourceSort && node.data.i != drag.data.i) {
            node.data.s += 1
            addUpd(node)
          }
        }
      }
      // 上方的拖动到下方
      else if (isSame) {
        drag.data.s = enterSourceSort - 1
        addUpd(drag)
        for (let i = 0; i < enter.parent.childNodes.length; i++) {
          const node = enter.parent.childNodes[i]
          if (checkFolderType(node, folderType) && node.data.s < enterSourceSort && node.data.s > dragSourceSort && node.data.i != drag.data.i) {
            node.data.s -= 1
            addUpd(node)
          }
        }
      }
    }

    // drag 在 enter 后
    else if (dropType === 'after') {
      // 下方拖动到上方, 即拖拽的节点排序大于放置的节点排序
      if (dragSourceSort > enterSourceSort) {
        drag.data.s = enterSourceSort + 1
        addUpd(drag)
        for (let i = 0; i < enter.parent.childNodes.length; i++) {
          const node = enter.parent.childNodes[i]
          if (checkFolderType(node, folderType) && node.data.s > enterSourceSort && node.data.s < dragSourceSort && node.data.i != drag.data.i) {
            node.data.s += 1
            addUpd(node)
          }
        }
      } else {
        drag.data.s = enterSourceSort
        addUpd(drag)
        for (let i = 0; i < enter.parent.childNodes.length; i++) {
          const node = enter.parent.childNodes[i]
          if (checkFolderType(node, folderType) && node.data.s <= enterSourceSort && node.data.s > dragSourceSort && node.data.i != drag.data.i) {
            node.data.s -= 1
            addUpd(node)
          }
        }
      }
    }

    // drag 在 enter 内部
    if (dropType === 'inner') {
      const dragSourcePid = drag.data.p
      const dragSourceParent = DocTreeRef.value.getNode(dragSourcePid)
      if (dragSourceParent) {
        for (let i = 0; i < dragSourceParent.childNodes.length; i++) {
          const node = dragSourceParent.childNodes[i]
          if (checkFolderType(node, folderType) && node.data.s > dragSourceSort) {
            node.data.s -= 1
            addUpd(node)
          }
        }
      }

      drag.data.p = enter.data.i

      let maxSort: number = 0
      if (!enter.childNodes || enter.childNodes.length == 0) {
        maxSort = 0
      } else if (enter.childNodes.length == 1 && enter.childNodes[0].data.i === drag.data.i) {
        maxSort = 0
      } else {
        maxSort = getMaxSort(drag, enter.childNodes)
      }
      drag.data.s = maxSort + 1
      addUpd(drag)
    }
  }
  // 不同的父级
  else {
    const dragSourcePid = drag.data.p
    // pid 为 0 时需要特殊处理, 无法通过 node 获取, 只能通过 docTreeData 获取
    if (dragSourcePid === '0') {
      for (let i = 0; i < docTreeData.value.length; i++) {
        const node = docTreeData.value[i]
        if (checkFolderTypeByOrigin(node, folderType) && node.s > dragSourceSort) {
          node.s -= 1
          needUpd.push({ i: node.i, p: node.p, n: node.n, s: node.s, ty: node.ty })
        }
      }
    } else {
      const dragSourceParent = DocTreeRef.value.getNode(dragSourcePid)
      if (dragSourceParent) {
        for (let i = 0; i < dragSourceParent.childNodes.length; i++) {
          const node = dragSourceParent.childNodes[i]
          if (checkFolderType(node, folderType) && node.data.s > dragSourceSort) {
            node.data.s -= 1
            addUpd(node)
          }
        }
      }
    }

    // 拖拽与被拖拽的文件夹属于不同的父级 > 将一个文件夹拖拽到另一个文件夹之前
    if (dropType === 'before') {
      drag.data.p = enter.data.p
      drag.data.s = enterSourceSort
      // 拖拽的文件夹需修改
      addUpd(drag)
      //
      for (let i = 0; i < enter.parent.childNodes.length; i++) {
        const node = enter.parent.childNodes[i]
        if (checkFolderType(node, folderType) && node.data.s >= enterSourceSort && node.data.i != drag.data.i) {
          node.data.s += 1
          addUpd(node)
        }
      }
    }

    if (dropType === 'after') {
      drag.data.p = enter.data.p
      drag.data.s = enterSourceSort + 1
      addUpd(drag)
      for (let i = 0; i < enter.parent.childNodes.length; i++) {
        const node = enter.parent.childNodes[i]
        if (checkFolderType(node, folderType) && node.data.s > enterSourceSort && node.data.i != drag.data.i) {
          node.data.s += 1
          addUpd(node)
        }
      }
    }

    if (dropType === 'inner') {
      drag.data.p = enter.data.i
      let maxSort: number = 0
      if (!enter.childNodes || enter.childNodes.length == 0) {
        maxSort = 0
      } else if (enter.childNodes.length == 1 && enter.childNodes[0].data.i === drag.data.i) {
        maxSort = 0
      } else {
        maxSort = getMaxSort(drag, enter.childNodes)
      }
      drag.data.s = maxSort + 1
      addUpd(drag)
    }
  }
  if (needUpd.length > 0) {
    updateFn(needUpd)
  }
  console.log(needUpd)
  console.log('==========================================')
}

/**
 * 从 nodes 中获取最大排序, 排除掉 drag
 */
export const getMaxSort = (drag: Node, nodes: Node[]): number => {
  const maxSort = Math.max.apply(
    Math,
    nodes
      .filter((n) => n.data.i != drag.data.i)
      .map((item) => {
        return item.data.s
      })
  )
  return maxSort
}

/**
 * 拖拽时, 检查文件夹类型与文档类型是否相同
 *
 * @param node 节点
 * @param folderType 文件夹类型
 * @returns true/false
 */
const checkFolderType = (node: Node, folderType: FolderType): boolean => {
  const ty = node.data.ty
  if (folderType === 1) {
    if (ty === 1 || ty === 3) {
      return true
    }
  } else if (folderType === 2) {
    if (ty === 2) {
      return true
    }
  }

  return false
}

const checkFolderTypeByOrigin = (node: DocTree, folderType: FolderType): boolean => {
  const ty = node.ty
  if (folderType === 1) {
    if (ty === 1 || ty === 3) {
      return true
    }
  } else if (folderType === 2) {
    if (ty === 2) {
      return true
    }
  }

  return false
}
