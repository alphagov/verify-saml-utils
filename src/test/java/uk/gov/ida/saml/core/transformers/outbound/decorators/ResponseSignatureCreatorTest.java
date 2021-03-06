package uk.gov.ida.saml.core.transformers.outbound.decorators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.signature.Signature;
import uk.gov.ida.saml.security.SignatureFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResponseSignatureCreatorTest {

    private ResponseSignatureCreator responseSignatureCreator;
    @Mock
    private SignatureFactory signatureFactory;

    @Before
    public void setup() {
        responseSignatureCreator = new ResponseSignatureCreator(signatureFactory);
    }

    @Test
    public void decorate_shouldGetSignatureAndAssignIt() {
        Response response = mock(Response.class);
        Issuer issuer = mock(Issuer.class);
        String id = "response-id";
        String issuerId = "some-issuer-id";
        when(issuer.getValue()).thenReturn(issuerId);
        when(response.getSignatureReferenceID()).thenReturn(id);
        when(response.getIssuer()).thenReturn(issuer);

        responseSignatureCreator.addUnsignedSignatureTo(response);

        verify(signatureFactory).createSignature(id);
    }

    @Test
    public void decorate_shouldAssignSignatureToResponse() {
        Response response = mock(Response.class);
        Signature signature = mock(Signature.class);
        String id = "response-id";
        when(response.getIssuer()).thenReturn(mock(Issuer.class));
        when(response.getSignatureReferenceID()).thenReturn(id);
        when(signatureFactory.createSignature(id)).thenReturn(signature);

        responseSignatureCreator.addUnsignedSignatureTo(response);

        verify(response).setSignature(signature);
    }
}
