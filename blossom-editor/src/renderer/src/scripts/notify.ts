import { ElNotification } from 'element-plus'

export class Notify {


  public static info = (message: string, title?: string) => {
    ElNotification.info({
      message: message,
      title: title ? title : '(۶=°ω°=)۶',
      offset: 30,
      position: 'bottom-right'
    })
  }

  public static success = (message: string, title?: string) => {
    ElNotification.success({
      message: message,
      title: title ? title : '(۶๑❛ᴗ❛๑)۶',
      offset: 30,
      position: 'bottom-right'
    })
  }

  public static warning = (message: string, title?: string) => {
    ElNotification.warning({
      message: message,
      title: title ? title : '(ﾉ๑`^´๑)ﾉ',
      offset: 30,
      position: 'bottom-right'
    })
  }

  public static error = (message: string, title?: string) => {
    ElNotification.error({
      message: message,
      title: title ? title : '(๑T^T๑) ',
      offset: 30, 
      // duration: 0,
      position: 'bottom-right'
    })
  }
}

export default Notify