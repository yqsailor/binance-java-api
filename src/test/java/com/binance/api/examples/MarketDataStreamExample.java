package com.binance.api.examples;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.AllMarketTickersEvent;
import com.binance.api.client.domain.event.CandlestickEvent;
import com.binance.api.client.domain.event.TopDepthStreamEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Market data stream endpoints examples.
 * <p>
 * It illustrates how to create a stream to obtain updates on market data such as executed trades.
 */
public class MarketDataStreamExample {

    public static void main(String[] args) throws InterruptedException, IOException {
        BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();

        // Listen for aggregated trade events for ETH/BTC
//        client.onAggTradeEvent("ethbtc", response -> System.out.println(response));

        // Listen for changes in the order book in ETH/BTC
//        client.onDepthEvent("ethbtc", response -> System.out.println(response));

        // Obtain 1m candlesticks in real-time for ETH/BTC
//        client.onCandlestickEvent("ethbtc", CandlestickInterval.ONE_MINUTE, response -> System.out.println(response));

        // Obtain streams events for ETH/BTC @depth10 @aggTrade"
        client.onStreamsEvent("ethusdt@aggTrade/ethusdt@kline_1m", new BinanceApiCallback<Map<String, Object>>() {
            @Override
            public void onResponse(final Map<String, Object> response) {
                try {
//                    System.out.println(response);
                    if (response == null) {
                        System.err.println("response was empty");
                        return;
                    }
                    String stream = (String) response.get("stream");
                    if (StringUtils.isEmpty(stream)) {
                        System.err.println("stream was empty");
                        return;
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    if (stream.equalsIgnoreCase("!miniTicker@arr")) {
                        // All Market Mini Tickers Stream
                    } else if (stream.equalsIgnoreCase("!ticker@arr")) {
                        // All Market Mini Tickers Stream
                    } else {
                        String[] streams = stream.toLowerCase().split("@");
                        String symbol = streams[0];
                        stream = streams[1];
                        if (stream.startsWith("kline")) {
                            // The Kline/Candlestick Stream
                            CandlestickEvent candlestickEvent = mapper.readValue(mapper.writeValueAsString(response.get("data")), CandlestickEvent.class);
                            System.out.println(candlestickEvent);
                        } else if (stream.equalsIgnoreCase("trade")) {
                            // The Trade Streams
                        } else if (stream.equalsIgnoreCase("aggTrade")) {
                            // The Aggregate Trade Streams
                            AggTradeEvent aggTradeEvent = mapper.readValue(mapper.writeValueAsString(response.get("data")), AggTradeEvent.class);
                            System.out.println(aggTradeEvent);
                        } else if (stream.equalsIgnoreCase("miniTicker")) {
                            // 24hr Mini Ticker statistics
                            AllMarketTickersEvent allMarketTickersEvent = mapper.readValue(mapper.writeValueAsString(response.get("data")), AllMarketTickersEvent.class);
                            System.out.println(allMarketTickersEvent);
                        } else if (stream.equalsIgnoreCase("ticker")) {
                            // Individual Symbol Ticker Streams
                        } else if (stream.equalsIgnoreCase("depth")) {
                            // Individual Symbol Ticker Streams
                        } else if (stream.startsWith("depth")) {
                            // Diff. Depth Stream
                            System.out.println(response.get("data"));
                            TopDepthStreamEvent depthEvent = mapper.readValue(mapper.writeValueAsString(response.get("data")), TopDepthStreamEvent.class);
                            depthEvent.setSymbol(symbol);
                            System.out.println("depthEvent:" + depthEvent);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(final Throwable cause) {
                cause.printStackTrace(System.err);
            }
        });
    }
}
