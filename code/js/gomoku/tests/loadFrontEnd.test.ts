import { test, expect } from '@playwright/test';

test('has title', async ({ page }) => {
  await page.goto('http://localhost:8000/');

  // Expect a title "to contain" a substring.
  await expect(page).toHaveTitle(/Gomoku/);
});

test('can load About', async ({ page }) => {
  // when: navigating to the home page
  await page.goto('http://localhost:8000/');

  // then: the page has a link to the 'about' page
  const aboutLink = page.getByRole('link', {name: 'About', exact:false})
  await expect(aboutLink).toBeVisible()

  // when: navigating to the 'about' page
  await aboutLink.click()

  // then: the list of authors appears
  await expect(page.getByText('Authors:')).toBeVisible();

});