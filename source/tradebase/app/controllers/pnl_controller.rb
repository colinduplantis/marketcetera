class PnlController < ApplicationController
  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}
  include ApplicationHelper
  
  def index
    # display the index page   
  end

  # entry point into the search
  def report
      nickname_str = get_non_empty_string_from_two(params, :account, :nickname, "nickname")
      suffix = (params[:suffix].nil?) ? '' : params[:suffix]
      @report = ReportWithToFromDates.new(params, suffix)
      if(!@report.valid?)
        render :action => :index
        return
      end
      @from_date = @report.from_date.as_date
      @to_date = @report.to_date.as_date
      if(nickname_str.blank?)
        aggregate
      else
        by_account(nickname_str, params, suffix)
      end
  end

  private
  def by_account(nickname_str, params, suffix)
    @report = PnlByAccount.new(nickname_str, params, suffix)
    if(!@report.valid?)
      render :action => :index
      return
    end
    theAcct = @report.account
            
    begin
      byAcctCashflows = CashFlow.get_cashflows_from_to_in_acct(theAcct, @from_date, @to_date)
      logger.debug("byAccount got cfs: "+byAcctCashflows.inspect)
      # Check to make sure we get some cashflow back
      cashflows = (byAcctCashflows.length != 1) ? [] \
                                                : byAcctCashflows[theAcct.nickname].values.sort { |x,y| x.symbol <=> y.symbol}
      @nickname = theAcct.nickname
    rescue Exception => ex
      logger.debug("Error generating cashflow for #{theAcct.nickname}: " + ex);
      flash.now[:error] = "Error generating cashflow for #{theAcct.nickname}: "+ ex.to_s
      cashflows = []
    end
    @cashflow_pages, @cashflows = paginate_collection(cashflows, params)
    render :template => 'pnl/pnl_by_account'
  end

  def aggregate
    begin
      result = CashFlow.get_cashflows_from_to_in_acct(nil, @from_date, @to_date)
      
      # we get a hashtable of cashflows for each account. Need to iterate over each account, sum its cashflows
      # and provide a unified array, with a P&L for each account.
      pnls = {}
      # initialize all P&Ls for each account to be zero
      result.keys.each { |key| pnls[key] = BigDecimal("0")}
      # sum up all the cashflows
      result.keys.each { |key| result[key].values.each { |cf| pnls[key] += cf.cashflow }}
      cashflows = []
      pnls.keys.sort.each { |key| cashflows << {:account => key, :cashflow => pnls[key]} }
    rescue Exception => ex
      logger.debug("Error generating aggregate cashflow: " + ex);
      flash.now[:error] = "Error generating aggregate cashflow: " + ex.to_s
      cashflows = []
    end

    @cashflow_pages, @cashflows = paginate_collection(cashflows, params)
    render :template => 'pnl/pnl_aggregate'
  end

end
