package org.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class SoapServiceTest {
    private static WireMockServer wireMockServer;
    private final SoapService soapService = new SoapService();
    private static final String REQUEST_BODY_105 = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:Demo">
                <soapenv:Header/>
                <soapenv:Body>
                    <urn:updatePeriod>
                        <genericInput>
                            <messageId>DEMO.2.105</messageId>
                            <resend>true</resend>
                        </genericInput>
                        <updatePeriodInput>
                            <periodReference>
                                <name5>105</name5>
                            </periodReference>
                        </updatePeriodInput>
                    </urn:updatePeriod>
                </soapenv:Body>
            </soapenv:Envelope>""";

    private static final String REQUEST_BODY_106 = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:Demo">
                <soapenv:Header/>
                <soapenv:Body>
                    <urn:updatePeriod>
                        <genericInput>
                            <messageId>DEMO.2.106</messageId>
                            <resend>true</resend>
                        </genericInput>
                        <updatePeriodInput>
                            <periodReference>
                                <name5>106</name5>
                            </periodReference>
                        </updatePeriodInput>
                    </urn:updatePeriod>
                </soapenv:Body>
            </soapenv:Envelope>""";

    private static final String REQUEST_BODY_107 = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:Demo">
                <soapenv:Header/>
                <soapenv:Body>
                    <urn:updatePeriod>
                        <genericInput>
                            <messageId>DEMO.2.107</messageId>
                            <resend>true</resend>
                        </genericInput>
                        <updatePeriodInput>
                            <periodReference>
                                <name5>107</name5>
                            </periodReference>
                        </updatePeriodInput>
                    </urn:updatePeriod>
                </soapenv:Body>
            </soapenv:Envelope>""";

    @BeforeAll
    static void initAll() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
    }

    @BeforeEach
    void init() {
        wireMockServer.stubFor(post(urlEqualTo("/WSServlet"))
                .withRequestBody(equalToXml(REQUEST_BODY_105))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withStatus(200).withBody("<xmlResponse1/>")));
        wireMockServer.stubFor(post(urlEqualTo("/WSServlet"))
                .withRequestBody(equalToXml(REQUEST_BODY_106))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withStatus(200).withBody("<xmlResponse2/>")));
        wireMockServer.stubFor(post(urlEqualTo("/WSServlet"))
                .withRequestBody(equalToXml(REQUEST_BODY_107))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withStatus(200).withBody("<xmlResponse3/>")));
    }

    @Test
    void shouldSend() {
        var res = soapService.send(List.of(REQUEST_BODY_105, REQUEST_BODY_106, REQUEST_BODY_107));
        assertNotNull(res);
        assertEquals(3, res.size());
        assertEquals("<xmlResponse1/>", res.get(0));
        assertEquals("<xmlResponse2/>", res.get(1));
        assertEquals("<xmlResponse3/>", res.get(2));
    }

    @Test
    void shouldSendInThreads() throws InterruptedException {
        var res = soapService.sendInThreads(List.of(REQUEST_BODY_105, REQUEST_BODY_106, REQUEST_BODY_107));
        assertNotNull(res);
        assertEquals(3, res.size());
        assertEquals("<xmlResponse1/>", res.get(0));
        assertEquals("<xmlResponse2/>", res.get(1));
        assertEquals("<xmlResponse3/>", res.get(2));
    }
}