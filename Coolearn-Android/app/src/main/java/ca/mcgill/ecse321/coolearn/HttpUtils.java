package ca.mcgill.ecse321.coolearn;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * HttpUtils contains a series of static methods for interacting
 * with RESTful services, specifically the CooLearn backend.
 * @author group 11
 * @version 1.1
 */
public class HttpUtils {
    /**
     * The base URL of the CooLearn Backend
     */
    public static final String DEFAULT_BASE_URL = "https://coolearn-backend.herokuapp.com/";

    private static String baseUrl;
    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        baseUrl = DEFAULT_BASE_URL;
    }

    /**
     * Returns the base url being used by the utility
     * @return the base url being used by the utility
     */
    public static String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the base url being used by the utility
     * @param baseUrl The base url to be used by the utility
     */
    public static void setBaseUrl(String baseUrl) {
        HttpUtils.baseUrl = baseUrl;
    }

    /**
     * Makes a get request from a relative url, using request parameters
     * (as opposed to a request body)
     * @param url The relative url to call
     * @param params A set of parameters to append at the end of the URL
     * @param responseHandler The response handler to be invoked when the request gets a response
     */
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Makes a delete request from a relative url, using request parameters
     * (as opposed to a request body)
     * @param url The relative url to call
     * @param params A set of parameters to append at the end of the URL
     * @param responseHandler The response handler to be invoked when the request gets a response
     */
    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Makes a post request from a relative url, using request parameters
     * (as opposed to a request body)
     * @param url The relative url to call
     * @param params A set of parameters to append at the end of the URL
     * @param responseHandler The response handler to be invoked when the request gets a response
     */
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Makes a put request from a relative url, using request parameters
     * (as opposed to a request body)
     * @param url The relative url to call
     * @param params A set of parameters to append at the end of the URL
     * @param responseHandler The response handler to be invoked when the request gets a response
     */
    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * Makes a post request from a relative url, using a request body
     * (as opposed to a request body)
     * @param context The context of the application making the request
     * @param url The relative url to call
     * @param jsonParams A set of parameters that will be used as the request body
     * @param responseHandler The response handler to be invoked when the request gets a response
     */
    public static void post(Context context, String url, JSONObject jsonParams, AsyncHttpResponseHandler responseHandler) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e) {

        }
        client.post(context, getAbsoluteUrl(url), entity, "application/json",
                responseHandler);

    }

    /**
     * Makes a put request from a relative url, using a request body
     * (as opposed to a request body)
     * @param context The context of the application making the request
     * @param url The relative url to call
     * @param jsonParams A set of parameters that will be used as the request body
     * @param responseHandler The response handler to be invoked when the request gets a response
     */
    public static void put(Context context, String url, JSONObject jsonParams, AsyncHttpResponseHandler responseHandler) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e) {

        }
        client.put(context, getAbsoluteUrl(url), entity, "application/json",
                responseHandler);
    }

    /**
     * Makes a get request from an absolute url, using request parameters
     * (as opposed to a request body)
     * @param url The absolute url to call
     * @param params A set of parameters to append at the end of the URL
     * @param responseHandler The response handler to be invoked when the request gets a response
     */
    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }


    /**
     * Makes a post request from an absolute url, using request parameters
     * (as opposed to a request body)
     * @param url The absolute url to call
     * @param params A set of parameters to append at the end of the URL
     * @param responseHandler The response handler to be invoked when the request gets a response
     */
    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return baseUrl + relativeUrl;
    }
}