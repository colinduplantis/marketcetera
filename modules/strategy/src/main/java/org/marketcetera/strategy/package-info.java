/* $License$ */

/**
 * Marketcetera Strategies.
 * 
 * <h2>Overview</h2>
 * <h3>Supported Languages</h3>
 *   <ul>
 *     <li id="supported_language_java">
 *       <b>Java</b>
 *       <br>
 *       Java strategy support is provided by a Java 6 <code>JSR-199</code>
 *       compliant Java compiler.
 *     </li>
 *     <li id="supported_language_ruby">
 *       <b>Ruby</b>
 *       <br>
 *       Ruby strategy support is provided by <code>JRuby 1.1.3</code>.
 *     </li>
 *   </ul>
 *        <h2 id="strategy_structure">Strategy Structure</h2>
 *       
 *       <h3 id="strategy_structure_overview">Overview</h3>
 *       
 *        <h3 id="strategy_structure_class_structure">Class Structure</h3>
 *        A strategy consists of any block of executable code in the given language.  The strategy may consist 
 *        of one or more classes specified in a single file.  These classes may refer to each other,  
 *        Marketcetera classes, or external classes stored in a jar file.  To be executable by the Marketcetera 
 *        platform, however, one of the classes in the strategy file must be a language-appropriate subclass of the  
 *        Marketcetera <code>Strategy</code> class as follows:
 *        <ul>
 *            <li id="strategy_structure_class_structure_java">Java - <code>org.marketcetera.strategy.java.Strategy</code></li>
 *            <li id="strategy_structure_class_structure_ruby">Ruby - <code>org.marketcetera.strategy.ruby.Strategy</code></li>
 *        </ul>
 *        The code to be executed is contained in a local file.  The basename of the file must comply with the
 *        rules of the corresponding compiler for that strategy's language implementation.  The implementation of the strategy itself and its
 *        supporting classes and external references, if any,  are likewise constrained by the strategy language.
 *        
 *        <h3 id="strategy_structure_api">API</h3>
 *        A strategy has access to tools and data from the Marketcetera platform.  This set of information and
 *        tools is collectively referred to as the strategy API.  The syntax for the API varies by language, but the function
 *        is the same.
 *        
 *        <h4 id="strategy_structure_api_data">Data</h4>
 *        Data refers to the set of strategy methods that are called by the Marketcetera platform.
 *        To receive data, override one or more of the following methods in the primary class of the strategy.
 *        <p>
 *            <em id="strategy_structure_api_data_onstart">onStart</em>
 *            <br>
 *            <pre>
 *                void onStart()
 *                on_start
 *            </pre>
 *            Called when the strategy starts.  This is a good place to establish data flows with
 *            <a href="#strategy_structure_api_actions_requestmarketdata"><code>requestMarketData</code></a>,
 *            <a href="#strategy_structure_api_actions_requestcepdata"><code>requestCEPData</code></a>, or 
 *            <a href="#strategy_structure_api_actions_requestprocessedmarketdata"><code>requestProcessedMarketData</code></a>. 
 *            Data requests initiated during <code>onStart</code> may cause data to be delivered before 
 *            <code>onStart</code> completes.
 *        <p>
 *            <em id="strategy_structure_api_data_onstop">onStop</em>
 *            <br>
 *            <pre>
 *                void onStop()
 *                on_stop
 *            </pre>
 *            Called when the strategy stops.  Any cleanup that the strategy needs to do may be done here. 
 *            It is not necessary to cancel initiated data flows as all data requests made by a strategy  
 *            are automatically canceled.  It is not permitted to make new data requests, send or cancel 
 *            orders, or send suggestions in <code>onStop</code>.
 *        <p>
 *            <em id="strategy_structure_api_data_onask">onAsk</em>
 *            <br>
 *            <pre>
 *                void onAsk(org.marketcetera.event.AskEvent)
 *                on_ask(ask)
 *            </pre>
 *            Called when the strategy receives an ask event.  Ask events are received
 *            if the strategy has submitted a market data or complex event processing request.
 *        <p>
 *            <em id="strategy_structure_api_data_onbid">onBid</em>
 *            <br>
 *            <pre>
 *                void onBid(org.marketcetera.event.BidEvent)
 *                on_bid(bid)
 *            </pre>
 *            Called when the strategy receives a bid event.  Bid events are received
 *            if the strategy has submitted a market data or complex event processing request.
 *        <p>
 *            <em id="strategy_structure_api_data_ontrade">onTrade</em>
 *            <br>
 *            <pre>
 *                void onTrade(org.marketcetera.event.TradeEvent)
 *                on_trade(trade)
 *            </pre>
 *            Called when the strategy receives a trade event.  Trade events are received
 *            if the strategy has submitted a market data or complex event processing request.
 *        <p>
 *            <em id="strategy_structure_api_data_onmarketstat">onMarketstat</em>
 *            <br>
 *            <pre>
 *                void onMarketstat(org.marketcetera.event.MarketstatEvent)
 *                on_marketstat(marketstat)
 *            </pre>
 *            Called when the strategy receives a marketstat event.  Marketstat events are received
 *            if the strategy has submitted a market data or complex event processing request.
 *        <p>
 *            <em id="strategy_structure_api_data_ondividend">onDividend</em>
 *            <br>
 *            <pre>
 *                void onDividend(org.marketcetera.event.DividendEvent)
 *                on_dividend(dividend)
 *            </pre>
 *            Called when the strategy receives a dividend event.  Dividend events are received
 *            if the strategy has submitted a market data or complex event processing request.
 *        <p>
 *            <em id="strategy_structure_api_data_oncancelreject">onCancelReject</em>
 *            <br>
 *            <pre>
 *                void onCancelReject(org.marketcetera.trade.OrderCancelReject)
 *                on_cancel_reject(cancel)
 *            </pre>
 *            Called when the strategy receives an order cancel reject event.  Order cancel reject events
 *            are received if the strategy is connected to the Marketcetera server.  Note that
 *            the strategy will received order cancel reject events for all rejected orders, not just the
 *            orders the strategy created.
 *        <p>
 *            <em id="strategy_structure_api_data_onexecutionreport">onExecutionReport</em>
 *            <br>
 *            <pre>
 *                void onExecutionReport(org.marketcetera.trade.ExecutionReport)
 *                on_execution_report(execution_report)
 *            </pre>
 *            Called when the strategy receives an execution report.  Execution report events
 *            are received if the strategy is connected to the Marketcetera server.  Note that
 *            the strategy will received execution report events for all orders, not just the
 *            orders the strategy created.
 *        <p>
 *            <em id="strategy_structure_api_data_oncallback">onCallback</em>
 *            <br>
 *            <pre>
 *                void onCallback(java.lang.Object)
 *                on_callback(data)
 *            </pre>
 *            Called when the strategy receives a scheduled callback from Marketcetera as requested by the strategy 
 *            via <a href="#strategy_structure_api_services_requestcallbackafter"><code>requestCallbackAfter(long,java.lang.Object)</code></a>
 *            or <a href="#strategy_structure_api_services_requestcallbackat"><code>requestCallbackAt(long,java.lang.Object)</code></a>. 
 *            The object returned is the same object that is passed to the request. 
 *        <p>
 *            <em id="strategy_structure_api_data_onother">onOther</em>
 *            <br>
 *            <pre>
 *                void onOther(java.lang.Object)
 *                on_other(data)
 *            </pre>
 *            Called when the strategy receives an object that does not fall into any of the above categories
 *        <h4 id="strategy_structure_api_services">Services</h4>
 *        Services refers to the set of methods a strategy may execute that provide data.
 *        <p>
 *            <em id="strategy_structure_api_services_getparameter">getParameter</em>
 *            <br>
 *            <pre>
 *                java.lang.String getParameter(java.lang.String)
 *                get_parameter(key)
 *            </pre>
 *            Retrieves the parameter specified by the given key or <code>null</code>/<code>nil</code>
 *            if no such parameter exists.  Parameters are specified to the
 *            strategy at start time as a map of key/value pairs.  The parameters specified
 *            are private to the given strategy.
 *        <p>
 *            <em id="strategy_structure_api_services_getproperty">getProperty</em>
 *            <br>
 *            <pre>
 *                java.lang.String getProperty(java.lang.String)
 *                get_property(key)
 *            </pre>
 *            Retrieves the property specified by the given key or <code>null</code>/<code>nil</code>
 *            if no such property exists. 
 *            Unlike <a href="#strategy_structure_api_services_getparameter"><code>getParameter</code></a>,
 *            properties are common to all running strategies in the same Marketcetera process.  Changes
 *            made by one strategy will be visible to all other strategies.
 *        <p>
 *            <em id="strategy_structure_api_services_getexecutionreports">getExecutionReports</em>
 *            <br>
 *            <pre>
 *                org.marketcetera.trade.ExecutionReport[] getExecutionReports(org.marketcetera.trade.OrderID)
 *                get_execution_reports(orderID)
 *            </pre>
 *            Retrieves the execution reports that correspond to the given order.
 *            The order must have been sent by this strategy during this strategy session.  In order to 
 *            receive execution reports, the strategy must be connected to the Marketcetera server.  The 
 *            values returned may not correspond to the aggregate set of execution reports supplied  
 *            via <a href="#strategy_structure_api_data_onexecutionreport"><code>onExecutionReport</code></a>
 *            as <code>getExecutionReports</code> is limited to execution reports from orders sent by this 
 *            strategy and <code>onExecutionReport</code> receives all execution reports. 
 *        <p>
 *            <em id="strategy_structure_api_services_getbrokers">getBrokers</em>
 *            <br>
 *            <pre>
 *                org.marketcetera.client.brokers.BrokerStatus[] getBrokers()
 *                get_brokers
 *            </pre>
 *            Retrieves the brokers known to the Marketcetera server.  The strategy must be connected 
 *            to the Marketcetera server to retrieve brokers. 
 *        <p>
 *            <em id="strategy_structure_api_services_getpositionasof">getPositionAsOf</em>
 *            <br>
 *            <pre>
 *                java.math.BigDecimal getPositionAsOf(java.util.Date,java.lang.String)
 *                get_position_as_of(date,symbol)
 *            </pre>
 *            Retrieves the most accurate position of the given symbol at the given point of time possible.
 *            The position may be temporarily inaccurate if there are ongoing changes in the position of the given symbol.
 *            The strategy must be connected to the Marketcetera server to retrieve positions.  If the position cannot 
 *            be retrieved, this method will return <code>null</code>/<code>nil</code>.  If there is no current position 
 *            in the given symbol at the given time, this method will return <code>0</code>.
 *        <p>
 *            <em id="strategy_structure_api_services_geturn">getURN</em>
 *            <br>
 *            <pre>
 *                org.marketcetera.module.ModuleURN getURN()
 *                get_urn()
 *            </pre>
 *            Retrieves the <code>ModuleURN</code> of the strategy
 *        <h4 id="strategy_structure_api_actions">Actions</h4>
 *        Actions refers to the set of methods a strategy may execute each of which effects a specific change.
 *        <p>
 *            <em id="strategy_structure_api_actions_setproperty">setProperty</em>
 *            <br>
 *            <pre>
 *                void setProperty(java.lang.String,java.lang.String)
 *                set_property(key,value)
 *            </pre>
 *            Sets the given key to the given value, creating the given key if it does exist, otherwise overwriting the existing value.
 *            All strategies share properties in common, so one strategy may change data another strategy created.
 *        <p>
 *            <em id="strategy_structure_api_actions_requestcallbackafter">requestCallbackAfter</em>
 *            <br>
 *            <pre>
 *                void requestCallbackAfter(long,java.lang.Object)
 *                request_callback_after(delay,data)
 *            </pre>
 *            Requests a callback from the Marketcetera platform after the given delay in milliseconds has elapsed.  The Marketcetera platform will 
 *            execute <a href="#strategy_structure_api_data_oncallback"><code>onCallback</code></a>, passing it the given object.
 *        <p>
 *            <em id="strategy_structure_api_actions_requestcallbackat">requestCallbackAt</em>
 *            <br>
 *            <pre>
 *                void requestCallbackAt(long,java.lang.Object)
 *                request_callback_at(time,data)
 *            </pre>
 *            Requests a callback from the Marketcetera platform at the given time, specified in milliseconds since <a href="http://en.wikipedia.org/wiki/Unix_time">epoch</a>.  The Marketcetera platform will 
 *            execute <a href="#strategy_structure_api_data_oncallback"><code>onCallback</code></a>, passing it the given object.
 *        <p>
 *            <em id="strategy_structure_api_actions_requestmarketdata">requestMarketData</em>
 *            <br>
 *            <pre>
 *                int requestMarketData(java.lang.String,java.lang.String)
 *                request_market_data(symbols,source)
 *            </pre>
 *            Requests full depth-of-book market data from the given market data source for the given symbol or symbols.  Symbols is a 
 *            comma-separated list of symbols specified in the format appropriate for the given market data source.  The data will  
 *            be delivered as incremental updates via <a href="#strategy_structure_api_data_onbid"><code>onBid</code></a>,
 *            <a href="#strategy_structure_api_data_onask"><code>onAsk</code></a>, and 
 *            <a href="#strategy_structure_api_data_ontrade"><code>onTrade</code></a>. 
 *            The market data will continue to arrive until the request is canceled via 
 *            <a href="#strategy_structure_api_actions_canceldatarequest"><code>cancelDataRequest(int)</code></a>
 *            or <a href="#strategy_structure_api_actions_cancelalldatarequests"><code>cancelAllDataRequests()</code></a>.
 *            This method returns an identifier that refers to this market data request.  The identifier can be used to cancel 
 *            the market data request.  If the request fails, the method returns <code>0</code>.
 *        <p>
 *            <em id="strategy_structure_api_actions_canceldatarequest">cancelDataRequest</em>
 *            <br>
 *            <pre>
 *                void cancelDataRequest(int)
 *                cancel_data_request(id)
 *            </pre>
 *            Cancels the data request associated with the given id.  If the id does not correspond to an active
 *            data request created by this strategy in this session, this method does nothing.  This method may
 *            be used to cancel either a market data or complex event processor request.
 *        <p>
 *            <em id="strategy_structure_api_actions_cancelalldatarequests">cancelAllDataRequests</em>
 *            <br>
 *            <pre>
 *                void cancelAllDataRequests()
 *                cancel_all_data_requests()
 *            </pre>
 *            Cancels all active data (both market data and complex event processor) requests 
 *            created by this strategy in this session.
 *        <p>
 *            <em id="strategy_structure_api_actions_requestcepdata">requestCEPData</em>
 *            <br>
 *            <pre>
 *                int requestCEPData(java.lang.String[],java.lang.String)
 *                request_cep_data(statements,source)
 *            </pre>
 *            Requests data from the given complex event processor source for the query specified by the given statements. 
 *            Statements is an array of strings specified in the format appropriate for the given complex event processor source.  The data will  
 *            be delivered as incremental updates via the appropriate <a href="#strategy_structure_api_data">data method</a>. 
 *            The complex event processor data will continue to arrive until the request is canceled via 
 *            <a href="#strategy_structure_api_actions_canceldatarequest"><code>cancelDataRequest(int)</code></a>
 *            or <a href="#strategy_structure_api_actions_cancelalldatarequests"><code>cancelAllDataRequests()</code></a>.
 *            This method returns an identifier that refers to this complex event processor request.  The identifier can be 
 *            used to cancel the complex event processor request.  If the request fails, the method returns <code>0</code>.
 *        <p>
 *            <em id="strategy_structure_api_actions_requestprocessedmarketdata">requestProcessedMarketData</em>
 *            <br>
 *            <pre>
 *                int requestProcessedMarketData(java.lang.String,java.lang.String,java.lang.String[],java.lang.String)
 *                request_processed_market_data(symbols,marketDataSource,statments,cepSource)
 *            </pre>
 *            Requests market data specified by the given comma-separated list of symbols from the given market data source 
 *            processed by the given complex event processor query executed in the given complex event processor source.  The format  
 *            of the arguments is identical to that of the union of 
 *            <a href="#strategy_structure_api_actions_requestmarketdata"><code>requestMarketData</code></a> and 
 *            <a href="#strategy_structure_api_actions_requestcepdata"><code>requestCEPData</code></a>.  The data will 
 *            be delivered as incremental updates via the appropriate <a href="#strategy_structure_api_data">data method</a>. 
 *            The data will continue to arrive until the request is canceled via 
 *            <a href="#strategy_structure_api_actions_canceldatarequest"><code>cancelDataRequest(int)</code></a> or 
 *            <a href="#strategy_structure_api_actions_cancelalldatarequests"><code>cancelAllRequests()</code></a>.  
 *            This method returns an identifier that refers to this request.  
 *            The identifier can be used to cancel the request.  If the request fails, the method returns <code>0</code>.
 *        <p>
 *            <em id="strategy_structure_api_actions_suggesttrade">suggestTrade</em>
 *            <br>
 *            <pre>
 *                void suggestTrade(org.marketcetera.trade.OrderSingle,java.math.BigDecimal,java.lang.String)
 *                suggest_trade(order,score,identifier)
 *            </pre>
 *            Suggests a trade for the given order with the given confidence score and identifying label.  Trade suggestions
 *            are sent by the strategy to the destination specified for trade suggestions when the strategy is created.
 *            The order should have sufficient information to prevent it from being rejected; no validation is done when a
 *            trade suggestion is created.  There is no way to cancel a trade suggestion nor can a trade suggestion that is
 *            converted to an order be canceled by the strategy that created the suggestion.  It is recommended that the
 *            score for a trade suggestion fall in the interval [0.0,1.0].  The identifier can be any value desired. 
 *        <p>
 *            <em id="strategy_structure_api_actions_send">send</em>
 *            <br>
 *            <pre>
 *                java.lang.boolean send(java.lang.Object)
 *                send(data)
 *            </pre>
 *            Sends an object to the destination specified when the strategy is created.  The value returned indicates whether
 *            the object was successfully transmitted or not.
 *        <p>
 *            <em id="strategy_structure_api_actions_cancelorder">cancelOrder</em>
 *            <br>
 *            <pre>
 *                org.marketcetera.trade.OrderCancel cancelOrder(org.marketcetera.trade.OrderID,java.lang.boolean)
 *                cancel_order(orderID,inSendOrder)
 *            </pre>
 *            Sends a request to cancel the given order.  The order must have been created by this strategy during this
 *            strategy session.  The cancel request will be sent to the same destination specified for orders.  The
 *            <code>OrderCancel</code> is returned.  It is submitted if <code>inSendOrder</code> is true, otherwise it
 *            is the caller's responsibility to submit the order manually with <code>sendOther</code>.  Note that successful execution of
 *            this method does not guarantee that an order will be canceled, just that the cancel request was sent.
 *        <p>
 *            <em id="strategy_structure_api_actions_cancelreplace">cancelReplace</em>
 *            <br>
 *            <pre>
 *                org.marketcetera.trade.OrderReplace cancelReplace(org.marketcetera.trade.OrderID,org.marketcetera.trade.OrderSingle,java.lang.boolean)
 *                cancel_replace(orderID,newOrder,sendOrder?)
 *            </pre>
 *            Sends a request to cancel and replace the order represented by the given <code>OrderID</code> with 
 *            the given new order.  The original order must have been created by this strategy during this
 *            strategy session.  The cancel replace request will be sent to the same destination specified for orders.
 *            The <code>OrderReplace</code> returned can be used to cancel the order or collect execution reports
 *            for it. Note that successful execution of this method does not guarantee that an order will be 
 *            canceled and replaced, just that the cancel and replace request was sent.  If <code>submitOrder</code> is
 *            false, the order will be returned but not submitted.  It is the caller's responsibility to submit the order manually.
 *            This would be done, for example, if the user needed to customize the <code>CancelOrder</code> before it is submitted.
 *        <p>
 *            <em id="strategy_structure_api_actions_cancelallorders">cancelAllOrders</em>
 *            <br>
 *            <pre>
 *                int cancelAllOrders()
 *                cancel_all_orders()
 *            </pre>
 *            Sends requests to cancel all orders created by by this strategy during this
 *            strategy session.  The cancel requests will be sent to the same destination specified for orders.  The return
 *            value indicates how many cancel requests were successfully sent.  Note that successful execution of
 *            this method does not guarantee that an order will be canceled, just that the cancel requests were sent.
 *        <p>
 *            <em id="strategy_structure_api_actions_sendmessage">sendMessage</em>
 *            <br>
 *            <pre>
 *                void sendMessage(quickfix.MessageMessage,org.marketcetera.trade.BrokerID)
 *                send_message(fixMessage,broker)
 *            </pre>
 *            Sends the given FIX message routed to the given broker.  The FIX message will be sent to the same location
 *            specified for orders.  The broker may be chosen from the list returned by
 *            <a href="#strategy_structure_api_services_getbrokers"><code>getBrokers()</code></a>.  This method is intended
 *            to be used in special cases when <a href="#strategy_structure_api_services_sendorder"><code>sendOrder</code></a>
 *            is not adequate.
 *        <p>
 *            <em id="strategy_structure_api_actions_sendeventtocep">sendEventToCEP</em>
 *            <br>
 *            <pre>
 *                void sendEventToCEP(org.marketcetera.event.EventBase,java.lang.String)
 *                send_event_to_cep(event,source)
 *            </pre>
 *            Sends the given event to the specified CEP source.  The default query namespace for the strategy is the target
 *            within the given CEP source.
 *        <p>
 *            <em id="strategy_structure_api_actions_sendevent">sendEvent</em>
 *            <br>
 *            <pre>
 *                void sendEvent(org.marketcetera.event.EventBase)
 *                send_event(event)
 *            </pre>
 *            Sends the given event to the event subscribers for this strategy.  Each strategy may have a number of
 *            subscribers that express interest in events published by it.  This method will send the given event to
 *            those subscribers.
 *        <p>
 *            <em id="strategy_structure_api_actions_notifylow">notifyLow</em>
 *            <br>
 *            <pre>
 *                void notifyLow(java.lang.String,java.lang.String)
 *                notify_low(subject,body)
 *            </pre>
 *            Transmits a low priority notification with the given subject and body.    The notifications are emitted
 *            to the strategy output stream.
 *        <p>
 *            <em id="strategy_structure_api_actions_notifymedium">notifyMedium</em>
 *            <br>
 *            <pre>
 *                void notifyMedium(java.lang.String,java.lang.String)
 *                notify_medium(subject,body)
 *            </pre>
 *            Transmits a medium priority notification with the given subject and body.    The notifications are emitted
 *            to the strategy output stream.
 *        <p>
 *            <em id="strategy_structure_api_actions_notifyhigh">notifyHigh</em>
 *            <br>
 *            <pre>
 *                void notifyHigh(java.lang.String,java.lang.String)
 *                notify_high(subject,body)
 *            </pre>
 *            Transmits a high priority notification with the given subject and body.  The notifications are emitted
 *            to the strategy output stream.
 *        <p>
 *            <em id="strategy_structure_api_actions_debug">debug</em>
 *            <br>
 *            <pre>
 *                void debug(java.lang.String)
 *                debug(message)
 *            </pre>
 *            Emits the given message to the strategy log stream.  This message will be emitted only if the appropriate
 *            logger category is activated for the strategy module.
 *        <p>
 *            <em id="strategy_structure_api_actions_info">info</em>
 *            <br>
 *            <pre>
 *                void info(java.lang.String)
 *                info(message)
 *            </pre>
 *            Emits the given message to the strategy log stream.  This message will be emitted only if the appropriate
 *            logger category is activated for the strategy module.
 *        <p>
 *            <em id="strategy_structure_api_actions_warn">warn</em>
 *            <br>
 *            <pre>
 *                void warn(java.lang.String)
 *                warn(message)
 *            </pre>
 *            Emits the given message to the strategy log stream.  This message will be emitted only if the appropriate
 *            logger category is activated for the strategy module.
 *        <p>
 *            <em id="strategy_structure_api_actions_error">error</em>
 *            <br>
 *            <pre>
 *                void error(java.lang.String)
 *                error(message)
 *            </pre>
 *            Emits the given message to the strategy log stream.  This message will be emitted only if the appropriate
 *            logger category is activated for the strategy module.
 *        <p>
 *            <em id="strategy_structure_api_actions_createdataflow">createDataFlow</em>
 *            <br>
 *            <pre>
 *                org.marketcetera.module.DataFlowID createDataFlow(java.lang.boolean,org.marketcetera.module.DataRequest...)
 *                create_data_flow(appendToDataSink?,requests)
 *            </pre>
 *            Creates a data flow between modules as described by the given <code>DataRequest</code> objects.  Appends data to the
 *            data sink if so indicated.  Normal rules apply with respect to modules being available for data flows.  The <code>DataFlowID</code>
 *            returned may be used to cancel the data flow.  When the strategy ends, all data flows are automatically halted.
 *        <p>
 *            <em id="strategy_structure_api_actions_canceldataflow">cancelDataFlow</em>
 *            <br>
 *            <pre>
 *                void cancelDataFlow(org.marketcetera.module.DataFlowID)
 *                cancel_data_flow(dataFlowID)
 *            </pre>
 *            Cancels the data flow with the given <code>DataFlowID</code>.
 *        
 *        <h2 id="operation">Strategy Operation</h2>
 *        
 *        <h3 id="operation_parameters">Parameters</h3>
 *        Strategies may be given a map of string-based key/value pairs at creation time.  These values are private to the
 *        given strategy instance and are accessible via 
 *        <a href="#strategy_structure_api_services_getparameter"><code>getParameter(java.lang.String)</code></a>.  The parameter values
 *        are immutable while the strategy is running.  No other strategies may see a strategy's parameters.  Properties
 *        are created external to the strategy and passed in.
 *        
 *        <h3 id="operation_properties">Properties</h3>
 *        Strategies have acces to an additional map of string-based key/value pairs called properties.
 *        These values are common to all running strategy instances and are accessible via 
 *        <a href="#strategy_structure_api_services_getproperty"><code>getProperty(java.lang.String)</code></a> and
 *        <a href="#strategy_structure_api_actions_setproperty"><code>setProperty(java.lang.String,java.lang.String)</code></a>.
 *        
 *        <h3 id="operation_destinations">Destinations</h3>
 *        When a strategy is created, destinations may be specified for orders and suggestions.  All suggestions created by
 *        the strategy are routed to the suggestion destination.  All orders, FIX messages, cancels, and cancel/replaces are
 *        routed to the orders destination.  If the orders or suggestion destination is not specified, then the corresponding
 *        category of data will not be sent.
 *        
 *        <h3 id="operation_moduleframework">Module Framework Interface</h3>
 *        Strategies can be run from within Strategy Agent - the container of our module framework. To run a strategy, create it then start it:
 *          <pre>
 *                createModule;metc:strategy:system;metc:strategy:system:&lt;instancename&gt;,&lt;classname&gt;,&lt;languagename&gt;,&lt;filepath&gt;,,,metc:sink:system
 *                startModule;metc:strategy:system:&lt;instancename&gt;
 *        </pre>
 *        where:
 *        <ul>
 *          <li><em>&lt;instancename&gt;</em> is the user-specified name of the strategy. The name must be unique among all running strategies</li>
 *          <li><em>&lt;languagename&gt;</em> is the name of the language in which the strategy is implemented. This value must match the name of a supported language</li>
 *          <li><em>&lt;classname&gt;</em> is the name of the primary class implemented in the strategy file. This class must extend the appropriate Strategy class in a language-appropriate fashion.</li>
 *          <li><em>&lt;filepath&gt;</em> is the location of the strategy script file.</li>
 *        </ul>
 *        
 *        The last parameter, metc:sink:system, is the destination for all strategy emissions. The sink destination is a catch-all data repository that allows the data to be created by the strategy and captured,
 *        but makes sure that the orders and suggestions do not get routed to a broker. Note that if the strategy executes market data requests, the corresponding market data provider must be started before the
 *        request is made. To stop the strategy, execute:
 *        <pre>
 *                stopModule;metc:strategy:system:&lt;instancename&gt;
 *        </pre>
 *
 *        See Strategy Agent documentation for more details on commands file syntax.
 *        
 *        <h2 id="technical">Technical Information</h2>
 *        
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
package org.marketcetera.strategy;
