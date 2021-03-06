package uk.gov.ida.saml.core.transformers.outbound.decorators;

import com.google.common.base.Throwables;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import uk.gov.ida.saml.security.EncrypterFactory;
import uk.gov.ida.saml.security.EncryptionCredentialFactory;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;

import java.util.List;

public abstract class AbstractAssertionEncrypter<T> {
    protected final EncryptionCredentialFactory credentialFactory;
    protected final EncrypterFactory encrypterFactory;
    protected final EntityToEncryptForLocator entityToEncryptForLocator;

    public AbstractAssertionEncrypter(
            final EncrypterFactory encrypterFactory,
            final EntityToEncryptForLocator entityToEncryptForLocator,
            final EncryptionCredentialFactory credentialFactory) {

        this.encrypterFactory = encrypterFactory;
        this.entityToEncryptForLocator = entityToEncryptForLocator;
        this.credentialFactory = credentialFactory;
    }

    public T encryptAssertions(T samlMessage) {
        if (getAssertions(samlMessage).size() > 0) {
            String entityToEncryptFor = entityToEncryptForLocator.fromRequestId(getRequestId(samlMessage));
            Credential credential = credentialFactory.getEncryptingCredential(entityToEncryptFor);

            Encrypter samlEncrypter = encrypterFactory.createEncrypter(credential);

            for (Assertion assertion : getAssertions(samlMessage)) {
                try {
                    EncryptedAssertion encryptedAssertion = samlEncrypter.encrypt(assertion);
                    getEncryptedAssertions(samlMessage).add(encryptedAssertion);
                } catch (EncryptionException e) {
                    throw Throwables.propagate(e);
                }
            }
            getAssertions(samlMessage).removeAll(getAssertions(samlMessage));
        }
        return samlMessage;
    }

    protected abstract String getRequestId(final T response);

    protected abstract List<EncryptedAssertion> getEncryptedAssertions(T response);

    protected abstract List<Assertion> getAssertions(T response);
}
