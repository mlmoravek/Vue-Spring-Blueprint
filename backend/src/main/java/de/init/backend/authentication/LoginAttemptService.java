package de.init.backend.authentication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import de.init.backend.authentication.config.AuthConfiguration;

@Service
class LoginAttemptService {

	private final int MAX_ATTEMPT;
	// we keep the number of wrong attempts per IP address for 24 hours
	private LoadingCache<String, Integer> attemptsCache;

	@Autowired
	public LoginAttemptService(AuthConfiguration authConfig) {
		super();
		this.MAX_ATTEMPT = authConfig.getMaxLoginAttempts();
		// if max attempts is -1 this feature got disabled
		if (MAX_ATTEMPT > 0) {
			attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
					.build(new CacheLoader<String, Integer>() {
						@Override
						public Integer load(final String key) {
							return 0;
						}
					});
		}
	}

	/**
	 * the successful authentication resets that counter
	 * 
	 * @param key IP
	 */
	public void loginSucceeded(final String key) {
		// if attemptsCache == null this feature is disabled
		if (this.attemptsCache == null)
			return;

		attemptsCache.invalidate(key);
	}

	/**
	 * an unsuccessful authentication attempt increases the number of attempts for
	 * that IP
	 * 
	 * @param key IP
	 */
	public void loginFailed(final String key) {
		// if attemptsCache == null this feature is disabled
		if (this.attemptsCache == null)
			return;

		int attempts = 0;
		try {
			attempts = attemptsCache.get(key);
		} catch (final ExecutionException e) {
			attempts = 0;
		}
		attempts++;
		attemptsCache.put(key, attempts);
	}

	public boolean isBlocked(final String key) {
		// if attemptsCache == null this feature is disabled
		if (this.attemptsCache == null)
			return false;

		try {
			return attemptsCache.get(key) >= MAX_ATTEMPT;
		} catch (final ExecutionException e) {
			return false;
		}
	}
}