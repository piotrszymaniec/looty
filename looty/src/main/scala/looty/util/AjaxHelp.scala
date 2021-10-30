package looty
package util

import org.scalajs.jquery.JQueryAjaxSettings

import scala.concurrent.{Promise, Future}
import scala.scalajs.js

//////////////////////////////////////////////////////////////
// Created by bjackman @ 12/9/13 11:15 PM
//////////////////////////////////////////////////////////////



object JsPromises {

  case class JsFutureFailure(reason: Any) extends Exception

  def wrap[A](that: js.Dynamic): Future[A] = {
    def wrapAPlusReason(reason: js.Any): Throwable = {
      reason match {
        case reason: Throwable => reason
        case x => new JsFutureFailure(reason)
      }
    }

    val p = Promise[A]()
    that.`then`(
      (data: js.Any) => p.success(data.asInstanceOf[A]),
      (reason: js.Any) => p.failure(wrapAPlusReason(reason)))
    p.future
  }
}

object AjaxHelp {

  object HttpRequestTypes extends Enumeration {
    type HttpRequestType = Value
    val Get = Value("Get")
    val Post = Value("Post")
    val Put = Value("Put")
    val Delete = Value("Delete")
  }

  def apply[A](url: String, requestType: HttpRequestTypes.HttpRequestType, data: Option[String]): Future[A] = {
    JsPromises.wrap[A] {
      val req = js.Dynamic.literal(url = url, `type`=requestType.toString).asJsDict[String]
      data.foreach(data => req("data") = data)
      jq.ajax(req.asInstanceOf[JQueryAjaxSettings])
    }
  }

  def get[A](url : String): Future[A] = apply(url, HttpRequestTypes.Get, None)
}


trait ReasonDetails extends js.Object {
  val status: Int = js.native
  val statusText: String = js.native
  //watch out it can be reallly long (whole webpage)
  val responseText: String = js.native
  def getResponseHeader(key:String):String = js.native
  //getResponseHeader("cf-chl-bypass")
  //when pathofexile.com is is blocked by captcha header returns value 1
}

object ReasonDetailsHelper {
  def siteIsCaptchaProtected(reason:ReasonDetails):Boolean = if (reason.asInstanceOf[ReasonDetails].getResponseHeader("cf-chl-bypass") == "1") true else false
}

//cheat-sheet if more things for ReasonDetails needed in future

//reason$3:
//abort: ƒ ( statusText )
//always: ƒ ()
//complete: ƒ ()
//done: ƒ ()
//error: ƒ ()
//fail: ƒ ()
//getAllResponseHeaders: ƒ ()
//getResponseHeader: ƒ ( key )
//overrideMimeType: ƒ ( type )
//pipe: ƒ ( /* fnDone, fnFail, fnProgress */ )
//progress: ƒ ()
//promise: ƒ ( obj )
//readyState: 4
//responseJSON: {error: {…}}
//responseText: "{\"error\":{\"code\":3,\"message\":\"Rate limit exceeded; You are requesting your character's items too frequently. Please try again later.\"}}"
//setRequestHeader: ƒ ( name, value )
//state: ƒ ()
//status: 429
//statusCode: ƒ ( map )
//statusText: "error"
//success: ƒ ()
//then: ƒ ( /* fnDone, fnFail, fnProgress */ )
//__proto__: Object
//s$1: null
//stackTrace$1: null
//stackdata: TypeError: this.undef is not a function at Object.createException__p1__O (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/looty-fastopt.js:21483:25) at Object.fillInStackTrace__jl_Throwable (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/looty-fastopt.js:26087:56) at Object.init___T__jl_Throwable (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/looty-fastopt.js:26188:8) at Object.init___ (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/looty-fastopt.js:28526:59) at Object.init___O (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/looty-fastopt.js:54915:44) at Object.looty$util$JsPromises$$wrapAPlusReason$1__sjs_js_Any__jl_Throwable (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/looty-fastopt.js:12412:67) at Object.<anonymous> (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/looty-fastopt.js:12423:55) at Object.<anonymous> (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/jslib/jquery.js:3069:33) at fire (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/jslib/jquery.js:2913:30) at Object.fireWith [as rejectWith] (chrome-extension://obaepdhffpdeafeohimidficflfklmfe/jslib/jquery.js:3025:7)
//__proto__: Object
